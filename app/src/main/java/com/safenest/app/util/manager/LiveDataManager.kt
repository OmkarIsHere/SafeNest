package com.safenest.app.util.manager

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.Member
import com.safenest.app.util.NetworkUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LiveDataManager(firebaseDatabase: FirebaseDatabase, private val sharedPrefManager: SharedPrefManager) {

    private val dbReference = firebaseDatabase.reference

    private val id = getDataFromPreference(AppConstant.USERID)
    private val fName = getDataFromPreference(AppConstant.USER_FIRSTNAME)
    private val lName = getDataFromPreference(AppConstant.USER_LASTNAME)
    private val phone = getDataFromPreference(AppConstant.USER_PHONE)
    private val icon = getDataFromPreference(AppConstant.USER_ICON)
    private val nestId = getDataFromPreference(AppConstant.USER_NEST)

    private val locationRef = dbReference
        .child(AppConstant.LIVE_LOCATION)
        .child(nestId)

    private fun getDataFromPreference(key: String) : String{
        return sharedPrefManager.getString(key, "")
    }

    fun setData(lat: String, lng : String, battery: String){
        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedDate = formatter.format(currentDate)
        val name = "$fName $lName"

        val member = Member(
            userId = id,
            userName = name,
            userPhone = phone,
            battery = battery,
            userIcon = icon,
            userLatLng = "$lat, $lng",
            internet = NetworkUtils.networkType.value,
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