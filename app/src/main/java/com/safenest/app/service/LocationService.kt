package com.safenest.app.service

import android.app.Service
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.os.IBinder
import androidx.core.app.ActivityCompat
import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
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

    private val tag = "LocationService"

    private lateinit var notification : Notification

    private val sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + "com.safenest.app" + "/" + R.raw.message_pop_alert)


    private val singleLocationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setIntervalMillis(1000).build()
    }

    private val singleLocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(location: LocationResult) {
                val lat = location.lastLocation?.latitude.toString()
                val lng = location.lastLocation?.longitude.toString()
                Log.d(tag, "onLocationResult: $lat, $lng")
            }
        }
    }

    private val recurringLocationRequest by lazy {
        LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60000)
            .setIntervalMillis(60000).build()
    }

    private val recurringLocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(location: LocationResult) {
                val lat = location.lastLocation?.latitude.toString()
                val lng = location.lastLocation?.longitude.toString()
                startServiceOfForeground(lat, lng)
                sendLocationBroadcast(false, lat, lng)
                Log.d(tag, "onLocationResult: $lat, $lng")
            }
        }
    }

    private fun sendLocationBroadcast(isError: Boolean, lat: String, lng: String) {
        val intent = Intent("LOCATION_UPDATE")
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", lng)
        intent.putExtra("isError", isError)
        sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val shouldStartLocationUpdates = intent?.getBooleanExtra(AppConstant.DO_WORK, false) ?: false
        recurringLocationUpdates()
        return START_REDELIVER_INTENT
    }

    private fun singleLocationUpdates() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(tag, "Location permissions not granted")
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(singleLocationRequest, singleLocationCallback, null)
            .addOnSuccessListener {
                Log.d(tag, "Location updates successfully requested.")
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Failed to request location updates: $exception")
            }

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(singleLocationRequest)
            .build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                fusedLocationProviderClient.requestLocationUpdates(singleLocationRequest, singleLocationCallback, null)
                    .addOnSuccessListener {
                        Log.d(tag, "Location updates successfully requested.")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(tag, "Failed to request location updates: $exception")
                    }
            }
            .addOnFailureListener { exception ->
                sendLocationBroadcast(true, "0.0", "0.0")
                Log.e(tag, "Location settings are not satisfied: $exception")
            }
    }

    private fun recurringLocationUpdates() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(tag, "Location permissions not granted")
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(recurringLocationRequest, recurringLocationCallback, null)
            .addOnSuccessListener {
                Log.d(tag, "Location updates successfully requested.")
            }
            .addOnFailureListener { exception ->
                Log.e(tag, "Failed to request location updates: $exception")
            }

        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(recurringLocationRequest)
            .build()

        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                fusedLocationProviderClient.requestLocationUpdates(recurringLocationRequest, recurringLocationCallback, null)
                    .addOnSuccessListener {
                        Log.d(tag, "Location updates successfully requested.")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(tag, "Failed to request location updates: $exception")
                    }
            }
            .addOnFailureListener { exception ->
                sendLocationBroadcast(true, "0.0", "0.0")
                Log.e(tag, "Location settings are not satisfied: $exception")
            }
    }

    private fun startServiceOfForeground(lat: String, lng: String) {

         notification = NotificationCompat.Builder(this, AppConstant.CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setSound(sound)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVibrate(null)
            .setContentTitle("Current Location")
            .setContentText("$lat, $lng")
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
        fusedLocationProviderClient.removeLocationUpdates(recurringLocationCallback)
        stopForeground(false)
        Log.d(tag, "Service destroyed")
    }
}