package com.safenest.app.ui.location

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.FcmMessage
import com.safenest.app.model.Member
import com.safenest.app.model.MessageData
import com.safenest.app.model.NotificationData
import com.safenest.app.repository.NotificationRepository
import com.safenest.app.util.LocationManager
import com.safenest.app.util.Miscellaneous
import com.safenest.app.util.SharedPrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

class LocationViewModel(
    private val locationManager: LocationManager,
    private val sharedPrefManager: SharedPrefManager,
    private val notificationRepository: NotificationRepository) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _members = MutableLiveData<List<Member>>()
    val members: LiveData<List<Member>> get() = _members

    fun setError(str: String){
        _error.value = str
    }

    fun updateDatabase(context: Context, lat: String, lng: String){
        val battery = Miscellaneous(context).getBatteryPercentage()
        locationManager.setData(lat, lng, battery)
    }

    fun readDatabase(){
        val locationRef = locationManager.getData()
        val userId = sharedPrefManager.getString(AppConstant.userId, "")
        locationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val locations = mutableListOf<Member>()
                for (data in dataSnapshot.children) {
                    val map = data.value as? Map<*, *> ?: continue
                    val userLocation = Member(
                        userId = data.key ?: "",
                        userName = map["userName"] as? String ?: "",
                        userLatLng = map["userLatLng"] as? String ?: "",
                        dateTime = map["dateTime"] as? String ?: "",
                        battery = map["battery"] as? String ?: "",
                        userPhone = map["userPhone"] as? String ?: "",
                        internet = map["internet"] as? String ?: "",
                        userIcon = map["userIcon"] as? String ?: ""
                    )
                    if(userId != data.key){
                        locations.add(userLocation)
                    }
                }
                _members.value = locations
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LocationService", "Failed to read value.", error.toException())
            }
        })
    }

     fun sendNotification(){
        viewModelScope.launch {
            val tokenDeferred = async(Dispatchers.IO) { generateBearerToken() }
            val token = tokenDeferred.await()

            if (token.isBlank()) {
                Log.e("NotificationService", "Failed to get Bearer Token")
                return@launch
            }
            val nestId = sharedPrefManager.getString(AppConstant.userNest, "")
            val fcmMessage = FcmMessage(
                MessageData(
                    topic = nestId,
                    NotificationData("Testinnggg", "Just Testinng")
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
//                token = credentials.accessToken.tokenValue
                return@withContext credentials.accessToken.tokenValue
            } catch (e: Exception) {
                Log.e("BEARERTOKEN", e.stackTraceToString())
                return@withContext ""
            }
        }
    }
}