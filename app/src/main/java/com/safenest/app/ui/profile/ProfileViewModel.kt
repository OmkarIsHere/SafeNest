package com.safenest.app.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.Location
import com.safenest.app.model.Member
import com.safenest.app.util.Extension
import com.safenest.app.util.SharedPrefManager

class ProfileViewModel(firebaseDatabase: FirebaseDatabase, private val sharedPrefManager: SharedPrefManager) : ViewModel() {

    private val TAG = "ProfileViewModel"
    private val dbReference = firebaseDatabase.reference
    private val locationRef = dbReference.child(AppConstant.LIVE_LOCATION).child(getDataFromPreference(AppConstant.userNest, ""))

    fun getDataFromPreference(key: String, value: String) : String{
        return sharedPrefManager.getString(key, value)
    }

    fun setData(){
        val id = getDataFromPreference(AppConstant.userId, "")
        val fName = getDataFromPreference(AppConstant.userFirstName, "")
        val lName = getDataFromPreference(AppConstant.userLastName, "")
        val name = "$fName $lName"
        val location = Member(
                userId = id,
                userName = name,
                userLatLng = "16.5435, 06.5641",
                dateTime = "20 Jan 2025"
            )
        locationRef.child(id).setValue(location)
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

        Log.d(TAG, "password: "+ Extension.hashPassword("849f1575ccfbf3a4d6cf00e6c5641b7fd4da2ed3e212c2d79ba9161a5a432ff0"))
    }
}