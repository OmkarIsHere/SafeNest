package com.safenest.app.ui.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.ResultState
import com.safenest.app.model.User
import com.safenest.app.service.NotificationService
import com.safenest.app.util.manager.LiveDataManager
import com.safenest.app.util.manager.SharedPrefManager

class ProfileViewModel(
    private val sharedPrefManager: SharedPrefManager,
    private val notificationService: NotificationService) : ViewModel() {

    fun getDataFromPreference(key: String, value: String) : String{
        return sharedPrefManager.getString(key, value)
    }

    fun logout(){
        notificationService.unSubscribeToTopic("Hello")
        sharedPrefManager.clearPreference()
    }
}