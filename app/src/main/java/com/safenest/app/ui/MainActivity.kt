package com.safenest.app.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.safenest.app.R
import com.safenest.app.constant.AppConstant
import com.safenest.app.databinding.ActivityMainBinding
import com.safenest.app.ui.permission.PermissionViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionViewModel: PermissionViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionViewModel = PermissionViewModel(application)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)

//        permissionViewModel.checkLocationPermission()
//        permissionViewModel.isPermissionGranted.observe(this) { isGranted ->
//            if (!isGranted) {
//                val intent = Intent(this@MainActivity, LocationService::class.java)
//                startService(intent)
//            } else {
//                val temp  = isGranted
//                // Proceed with app functionality
//            }
//        }

        val availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        Log.d("LOCATION_SERVICE", "Google Play services available ${ availability == ConnectionResult.SUCCESS}")

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstant.CHANNEL_ID,
                AppConstant.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }


}