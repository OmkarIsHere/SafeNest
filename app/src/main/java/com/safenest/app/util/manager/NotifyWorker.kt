package com.safenest.app.util.manager

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.auth.oauth2.GoogleCredentials
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.FcmMessage
import com.safenest.app.model.MessageData
import com.safenest.app.model.NotificationData
import com.safenest.app.model.PayLoadData
import com.safenest.app.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream

class NotifyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {

    private val notificationRepository: NotificationRepository by inject()
    private val sharedPrefManager: SharedPrefManager by inject()

    override suspend fun doWork(): Result {
        try {
            getCurrentLocation(applicationContext)
            return Result.success()
        }catch (e : Exception){
            return Result.retry()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(context: Context) {
        if (!checkLocationPermission(context)) {
            Log.e("DOWORK", "Permission denied for location")
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(3000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val body = "Latitude: $latitude, Longitude: $longitude"

                        Log.d("DOWORK", "getCurrentLocation: Lat: $latitude, Lng: $longitude")
                        sendNotification(body)
                    } else {
                        Log.e("DOWORK", "Location not available")
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun getCurrentLocationOLD(context: Context){
        val fusedLocationClient: FusedLocationProviderClient
        if(checkLocationPermission(context = context)){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val body = "Latitude: $latitude, Longitude: $longitude"

                    Log.d("DOWORK", "getCurrentLocation: Lat: $latitude, Lng: $longitude")
                    sendNotification(body)
                } else {
                    Log.e("DOWORK", "Location not available")
                }
            }.addOnFailureListener {
                Log.e("DOWORK", "Failed to get location")
            }
        }
    }

    private fun checkLocationPermission(context : Context): Boolean {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun sendNotification(body: String){
        CoroutineScope(Dispatchers.IO).launch {
            val token = generateBearerToken()

            if (token.isBlank()) {
                Log.e("NotificationService", "Failed to get Bearer Token")
                return@launch
            }
            val nestId = sharedPrefManager.getString(AppConstant.USER_NEST, "")
            val firstName = sharedPrefManager.getString(AppConstant.USER_FIRSTNAME, "")
            val userId = sharedPrefManager.getString(AppConstant.USERID, "")
            val fcmMessage = FcmMessage(
                MessageData(
                    topic = nestId,
                    NotificationData("$firstName's current location", body),
                    PayLoadData(userId, AppConstant.NOTIFICATION_ID, AppConstant.NOTIFICATION_NAME, "2")
                )
            )

            try {
                notificationRepository.sendNotification(token, fcmMessage)
            }catch (e:Exception){
                Log.e("NotificationService", e.stackTraceToString())
            }
        }
    }

    private suspend fun generateBearerToken(): String {
        return withContext(Dispatchers.IO) {
            try {
                val jsonKey = """
                {
                  "type": "service_account",
                  "project_id": "safenest-2ae47",
                  "private_key_id": "f8428e43b5a5f3cbd4a744d974b66c099e1a12ae",
                  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQCt44ieXdfiYPs7\njOZp/8ohl3vtsuEDc5imBG5/GKvSKsilJTujrWkP7ZnrUumXT4kR7x5sHtnGatBu\nyE/bQL4SIMJ2HIfqg6mocFmVfv2YS97hhn/ZGVXiZXdDDIioHuIbscPWMKUOgopN\neQgAmzA2CajmTU1LwjzYK4fVfjqdHongK+PYnqLg8AwZq4v+B9lrgWhCHbw8K8D+\nL77GWREDrL4fZPCe1WSNqzdMy/NrftxG3mCunR52S6szO8wcPbB9COaQ0v2ZZBRu\nHBim69wiS/OMIfd9HL/yZw7/8Fnzvi1hK30F+qjEi1BE2LCPon3Z+fhMqAZ2vWwO\nsa2noXjfAgMBAAECggEAAgpWcw1E2DotGIDJv1qqA4TkhmB/9Wto0iHhLgqbEYdA\n/XcXYQ9K2U3/bVx2szjSsewtXjVqdsKQGYyuKzNyR4Is5fflvGG5Hunakz9/OEdD\noT/Txjde454vLRE7lUVvf8WxN5UwiqALgWc3KdKoWSn52mHz0zy/PAYevOmTRH6v\nRYwfEO3mBeKjm0S0RTxTsuOimofb1MZXYerYU1iUomZdkhePkjbtQqeesFzWl91I\ngyViXLXXE+KDzjGyJ/Lurs8A/eY2dU1Hh6XGEVt2D+cWCLzRQR5FFm5On/XmKgWJ\nMd0Cv2bNyBAUFKplXP2TW4No69/2E9D03XuijG1yQQKBgQDlO97MsyWodyCyl1uo\nY8oTo1UAx76BqakjAWMQl/X3DnxS5a0rY45YL3XvyEmzHQFp+cS17SVEWPC5HKpB\nl9WAysXaJ3K0jPmd0sM5W/giY3mJWhI0T3NGonY6aKuQfn78iMMcP5Yy9YICqEDD\nTtEUFjqiJZ0PxDMloNTMv3i7vwKBgQDCMVHhMyIbcT8KT0ZjdMH0phtDIv+Q6laf\nO6RRkknOuplKyCe8pqNvtiIwjaeM+PnTXXJh0baB/N3g385YXhgUfBqASwOew+5Z\nAMfSmVcffdtslaXnJymSYR7Ahx12l6Kn4/2ljEjEwBo7uGX7cHFsELmIU1uKlDwm\nUxB3sJEK4QKBgQCbwKcsbJiss2yLC95iNpNJ7pNF+XHOhel++GVIFAgyeiws4xNb\nRMSl1HGMn4i743xfdi6a8et9WfUNwZVJBhIx9RSjmmQMmzDLdDXjVkLtkqs0kPeH\nhWgs2Rv9qbrQbbJ4gbAYFHhIXZmdlpaSXY4f2M6z91yVJtkduv57s6kj3wKBgDGv\nbMOx0Ygz8W0x21CXDwkJdvA2hC0PyBn1qJU2WKwMEiyQCZq7CBYNA7Joi/YFveXW\ngu2EOq4HhL2EhccWTBLxrdYlW0fD2bfr+zRnB2OHUBz4LPp3iqtpLfUUnPU61uMd\n8kfpHLU6cXvWMkGjA2Ii5VV7/m/2fW1Q02XMR1ABAn9ihs/ztquYSrpeGNNzHYiE\n1fhzlq+acV7ePMnTF6XgWTvSHqM/hiAfunNo6ngpt+vD18hXWAoIqG9al6CQHP31\nkm9t/IY2QzBy+IaCR4tC0cwM1CRK3znC/7fNWhWR+5wtt6nDXEFRQM5/U9D9ACYy\n5dOiyfu4oeE8o8UcWYJO\n-----END PRIVATE KEY-----\n",
                  "client_email": "firebase-adminsdk-xrd8f@safenest-2ae47.iam.gserviceaccount.com",
                  "client_id": "106460231162067275033",
                  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                  "token_uri": "https://oauth2.googleapis.com/token",
                  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-xrd8f%40safenest-2ae47.iam.gserviceaccount.com",
                  "universe_domain": "googleapis.com"
                }
                """.trimIndent()

                val credentials = GoogleCredentials
                    .fromStream(ByteArrayInputStream(jsonKey.toByteArray()))
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))

                credentials.refreshIfExpired()
                Log.d("BEARERTOKEN", "generateBearerToken: ${credentials.accessToken.tokenValue}")
                return@withContext credentials.accessToken.tokenValue
            } catch (e: Exception) {
                Log.e("BEARERTOKEN", e.stackTraceToString())
                return@withContext ""
            }
        }
    }

    /*
    private fun showNotification(title :String, body: String) {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            AppConstant.N_ID
        )
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelDescription = "Channel Description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(AppConstant.N_ID, AppConstant.N_NAME, channelImportance).apply {
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }


        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(3, notification.build())
        }

    }
     */

}