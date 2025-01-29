package com.safenest.app.ui.location

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
//import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.Member
import com.safenest.app.service.NotificationService
import com.safenest.app.util.LocationManager
import com.safenest.app.util.Miscellaneous
import com.safenest.app.util.SharedPrefManager
import java.io.FileInputStream

class LocationViewModel(private val locationManager: LocationManager, private val sharedPrefManager: SharedPrefManager, private val notificationService: NotificationService) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _members = MutableLiveData<List<Member>>()
    val members: LiveData<List<Member>> get() = _members

    fun setError(str: String){
        _error.value = str
    }

    fun getToken(){
//        notificationService.getToken()
//        notificationService.subscribeToTopic()
//        getFirebaseAccessToken()
    }

//    fun getFirebaseAccessToken(): String {
//        val serviceAccount = FileInputStream("safenest.json")
//        val credentials = GoogleCredentials.fromStream(serviceAccount)
//            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
//        credentials.refreshIfExpired()
//        Log.d("NotificationService", "getFirebaseAccessToken: ${credentials.accessToken.tokenValue}")
//        return credentials.accessToken.tokenValue
//    }

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


}