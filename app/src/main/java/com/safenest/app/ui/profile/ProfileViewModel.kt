package com.safenest.app.ui.profile

import androidx.lifecycle.ViewModel
import com.safenest.app.util.SharedPrefManager

class ProfileViewModel(private val sharedPrefManager: SharedPrefManager) : ViewModel() {

//    private val TAG = "ProfileViewModel"
//    private val dbReference = firebaseDatabase.reference
//    private val locationRef = dbReference.child(AppConstant.LIVE_LOCATION).child(getDataFromPreference(AppConstant.userNest, ""))

    fun getDataFromPreference(key: String, value: String) : String{
        return sharedPrefManager.getString(key, value)
    }

//    fun setData(){
//        val id = getDataFromPreference(AppConstant.userId, "")
//        val fName = getDataFromPreference(AppConstant.userFirstName, "")
//        val lName = getDataFromPreference(AppConstant.userLastName, "")
//        val name = "$fName $lName"
//        val member = Member(
//                userId = id,
//                userName = name,
//                userLatLng = "16.5435, 06.5641",
//                dateTime = "20 Jan 2025"
//            )
//        locationRef.child(id).setValue(member)
//    }
//
//    fun getData(){
//        locationRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val value = dataSnapshot.value
//                Log.d(TAG, "Value is: $value")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w(TAG, "Failed to read value.", error.toException())
//            }
//        })
//    }
}