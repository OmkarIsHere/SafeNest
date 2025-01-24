package com.safenest.app.ui.nest.upload_image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.ResultState
import com.safenest.app.model.User
import com.safenest.app.util.LocationManager
import com.safenest.app.util.SharedPrefManager

class UploadImageViewModel(private val sharedPrefManager: SharedPrefManager, private val firestore: FirebaseFirestore, private val locationManager: LocationManager) : ViewModel() {

    private val _resultState = MutableLiveData<ResultState>()
    val resultState: LiveData<ResultState> get() = _resultState

    fun updateUserIcon(imgUrl: String){
        val userId = sharedPrefManager.getString(AppConstant.userId, "")
        val user = User(userIcon = imgUrl)
        val userCollection = firestore.collection(AppConstant.USER).document(userId)
        userCollection.update("userIcon", imgUrl)
            .addOnSuccessListener {
                sharedPrefManager.putString(AppConstant.userIcon, user.userIcon ?: "")
                locationManager.updateUserIcon(imgUrl)
                _resultState.postValue(ResultState.Success("Profile icon updated successfully"))
            }
            .addOnFailureListener { exception ->
                _resultState.postValue(ResultState.Failure(exception.message!!))
            }
    }

}