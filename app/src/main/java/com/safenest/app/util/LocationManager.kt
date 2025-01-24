package com.safenest.app.util

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.Member
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationManager(firebaseDatabase: FirebaseDatabase, private val sharedPrefManager: SharedPrefManager) {

    private val dbReference = firebaseDatabase.reference

    private val id = getDataFromPreference(AppConstant.userId)
    private val fName = getDataFromPreference(AppConstant.userFirstName)
    private val lName = getDataFromPreference(AppConstant.userLastName)
    private val icon = getDataFromPreference(AppConstant.userIcon)
    private val nestId = getDataFromPreference(AppConstant.userNest)

    private val locationRef = dbReference
        .child(AppConstant.LIVE_LOCATION)
        .child(nestId)

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
            userIcon = icon,
            userLatLng = "$lat, $lng",
            dateTime = formattedDate
        )
        locationRef.child(id).setValue(member)
    }

    fun updateUserIcon(imgUrl : String){
        val updates = mapOf(
            "userIcon" to imgUrl
        )
        locationRef.child(id).updateChildren(updates)
    }

    fun getData(): DatabaseReference{
        return locationRef
    }
}