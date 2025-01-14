package com.safenest.app.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.safenest.app.util.LocationManager

class LocationViewModel(private val locationManager: LocationManager) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun setError(str: String){
        _error.value = str
    }

    fun updateDatabase(lat: String, lng: String){
        locationManager.setData(lat, lng)
    }
}