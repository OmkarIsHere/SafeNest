package com.safenest.app.ui.location

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.safenest.app.model.Member
import com.safenest.app.util.LocationManager

class LocationViewModel(private val locationManager: LocationManager) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _members = MutableLiveData<List<Member>>()
    val members: LiveData<List<Member>> get() = _members

    fun setError(str: String){
        _error.value = str
    }

    fun updateDatabase(lat: String, lng: String){
        locationManager.setData(lat, lng)
    }

    fun readDatabase(){
       val locationRef = locationManager.getData()
        locationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value
                val locations = mutableListOf<Member>()
                for (data in dataSnapshot.children) {
                    val map = data.value as? Map<*, *> ?: continue
                    val userLocation = Member(
                        userId = data.key ?: "",
                        userName = map["userName"] as? String ?: "",
                        userLatLng = map["userLatLng"] as? String ?: "",
                        dateTime = map["dateTime"] as? String ?: ""
                    )
                    locations.add(userLocation)
                }
                _members.value = locations
                Log.d("LocationService", "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LocationService", "Failed to read value.", error.toException())
            }
        })
    }
}