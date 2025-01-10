package com.safenest.app.service

import android.app.Service
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.os.IBinder
import androidx.core.app.ActivityCompat
import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.safenest.app.R
import com.safenest.app.constant.AppConstant

class LocationService : Service() {

    private lateinit var notification : Notification

    private val locationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setIntervalMillis(10000).build()
    }

    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
                Log.d("LOCATION_SERVICE", "onLocationAvailability: ${p0.isLocationAvailable}")
            }

            override fun onLocationResult(location: LocationResult) {
                val lat = location.lastLocation?.latitude.toString()
                val lng = location.lastLocation?.longitude.toString()
                Log.d("LOCATION_SERVICE", "onLocationResult: $lat, $lng")
                startServiceOfForeground(lat, lng)
//
//                val lastLocation = location.lastLocation
//                if (lastLocation != null) {
//                    val lat = lastLocation.latitude.toString()
//                    val lng = lastLocation.longitude.toString()
//                    Log.d("LOCATION_SERVICE", "onLocationResult: Latitude = $lat, Longitude = $lng")
//                    startServiceOfForeground(lat, lng)
//                } else {
//                    Log.e("LOCATION_SERVICE", "Location result is null.")
//                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationUpdates()
        return START_REDELIVER_INTENT
    }

//    private fun locationUpdates(){
//        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient( this)
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
//        fusedLocationProviderClient.requestLocationUpdates(
//            locationRequest, locationCallback, null
//        )
//    }

    private fun locationUpdates() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("LOCATION_SERVICE", "Location permissions not granted")
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, null
        ).addOnSuccessListener {
            Log.d("LOCATION_SERVICE", "Location updates successfully requested.")
        }
        .addOnFailureListener { exception ->
            Log.e("LOCATION_SERVICE", "Failed to request location updates: $exception")
        }

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        // Check if location settings are satisfied
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                Log.d("LOCATION_SERVICE", "Location settings are satisfied.")
                // Proceed to request location updates
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
                    .addOnSuccessListener {
                        Log.d("LOCATION_SERVICE", "Location updates successfully requested.")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("LOCATION_SERVICE", "Failed to request location updates: $exception")
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("LOCATION_SERVICE", "Location settings are not satisfied: $exception")
            }
    }

    private fun startServiceOfForeground(lat: String, lng: String) {
         notification = NotificationCompat.Builder(this, AppConstant.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Location Updates")
            .setContentText("$lat - $lng")
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startForeground(1, notification)
            }
        } else {
            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopForeground(true)
        Log.d("LOCATION_SERVICE", "Service destroyed")
    }
}