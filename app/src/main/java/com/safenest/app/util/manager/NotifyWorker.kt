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
import androidx.core.content.ContextCompat.getString
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.auth.oauth2.GoogleCredentials
import com.safenest.app.R
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

                        Log.d("DOWORK", "CurrentLocation: Lat: $latitude, Lng: $longitude")
                        sendNotification(context, body)
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
                    sendNotification(context, body)
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

    private fun sendNotification(context: Context, body: String){
        CoroutineScope(Dispatchers.IO).launch {
            val token = generateBearerToken(context = context)

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

    private suspend fun generateBearerToken(context : Context): String {
        return withContext(Dispatchers.IO) {
            try {
                val jsonKey = """
                {
                  "type": "${getString(context, R.string.type)}",
                  "project_id": "${getString(context, R.string.projectId)}",
                  "private_key_id": "${getString(context, R.string.privateKeyId)}",
                  "private_key": "${getString(context, R.string.privateKey)}",
                  "client_email": "${getString(context, R.string.clientEmail)}",
                  "client_id": "${getString(context, R.string.clientId)}",
                  "auth_uri": "${getString(context, R.string.authUri)}",
                  "token_uri": "${getString(context, R.string.tokenUri)}",
                  "auth_provider_x509_cert_url": "${getString(context, R.string.authProviderX509CertUrl)}",
                  "client_x509_cert_url": "${getString(context, R.string.clientX509CertUrl)}",
                  "universe_domain": "${getString(context, R.string.universeDomain)}"
                }
                """.trimIndent()

                Log.d("BEARERTOKEN", "jsonKey: \n $jsonKey")

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