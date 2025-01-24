package com.safenest.app.ui.profile

import androidx.lifecycle.ViewModel
import com.safenest.app.util.SharedPrefManager

class ProfileViewModel(private val sharedPrefManager: SharedPrefManager) : ViewModel() {

    fun getDataFromPreference(key: String, value: String) : String{
        return sharedPrefManager.getString(key, value)
    }

}