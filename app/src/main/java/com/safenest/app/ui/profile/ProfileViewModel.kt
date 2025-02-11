package com.safenest.app.ui.profile

import androidx.lifecycle.ViewModel
import com.safenest.app.service.NotificationService
import com.safenest.app.util.manager.SharedPrefManager

class ProfileViewModel(private val sharedPrefManager: SharedPrefManager, private val notificationService: NotificationService) : ViewModel() {

    fun getDataFromPreference(key: String, value: String) : String{
        return sharedPrefManager.getString(key, value)
    }

    fun logout(){
        notificationService.unSubscribeToTopic("Hello")
        sharedPrefManager.clearPreference()
    }
}