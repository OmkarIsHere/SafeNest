package com.safenest.app.util

import android.content.SharedPreferences

class SharedPrefManager(private val sharedPreferences: SharedPreferences) {

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun clearPreference(){
        sharedPreferences.edit().clear().apply()
    }

}