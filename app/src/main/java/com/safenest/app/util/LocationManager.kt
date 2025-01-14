package com.safenest.app.util

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.Member
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationManager(firebaseDatabase: FirebaseDatabase, private val sharedPrefManager: SharedPrefManager) {
    private val TAG = "LocationManager"

    private val dbReference = firebaseDatabase.reference

    private val id = getDataFromPreference(AppConstant.userId)
    private val fName = getDataFromPreference(AppConstant.userFirstName)
    private val lName = getDataFromPreference(AppConstant.userLastName)

    private val locationRef = dbReference
        .child(AppConstant.LIVE_LOCATION)
        .child(getDataFromPreference(AppConstant.userNest))

    private fun getDataFromPreference(key: String) : String{
        return sharedPrefManager.getString(key, "")
    }

    fun setData(lat: String, lng : String){
        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(currentDate)
        val name = "$fName $lName"

        val member = Member(
            userId = id,
            userName = name,
            userLatLng = "$lat, $lng",
            dateTime = formattedDate
        )
        locationRef.child(id).setValue(member)
    }

    fun getData(){
        locationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.value
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }
}