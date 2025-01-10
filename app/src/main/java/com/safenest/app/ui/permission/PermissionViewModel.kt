package com.safenest.app.ui.permission

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import android.Manifest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PermissionViewModel(application: Application) : AndroidViewModel(application) {
    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted: LiveData<Boolean> get() = _isPermissionGranted

    private val permissions = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    fun checkLocationPermission() {
        val context = getApplication<Application>()
        _isPermissionGranted.value = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        Log.d("LOCATION_SERVICE", "isGranted? : " + _isPermissionGranted.value)
    }
}