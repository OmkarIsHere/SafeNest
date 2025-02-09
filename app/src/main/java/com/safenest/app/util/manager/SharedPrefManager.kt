package com.safenest.app.util.manager

import android.content.SharedPreferences

class SharedPrefManager(private val sharedPreferences: SharedPreferences) {

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getBool(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun putBool(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun clearPreference(){
        sharedPreferences.edit().clear().apply()
    }

}