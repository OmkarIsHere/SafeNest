package com.safenest.app.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.safenest.app.R
import com.safenest.app.constant.AppConstant
import com.safenest.app.databinding.ActivityMainBinding
import com.safenest.app.service.LocationService
import com.safenest.app.ui.location.LocationViewModel
import com.safenest.app.util.KeepStateNavigator
import com.safenest.app.util.NetworkUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val locationViewModel: LocationViewModel by viewModel()

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val latitude = intent?.getStringExtra("latitude")
            val longitude = intent?.getStringExtra("longitude")
            val isError = intent?.getBooleanExtra("isError", false)
            if(!isError!!){
                locationViewModel.updateDatabase(context!!,latitude?: "0", longitude?:"0")
                locationViewModel.setError("")
            }else{
                locationViewModel.setError("Please Enable Location Service")
            }
        }
    }

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        val navView: BottomNavigationView = binding.navView

//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        navView.setupWithNavController(navController)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val keepStateNavigator = KeepStateNavigator(
            this,
            supportFragmentManager,
            R.id.nav_host_fragment_activity_main
        )
        navController.navigatorProvider.addNavigator(keepStateNavigator)

        val navGraph = navController.navInflater.inflate(R.navigation.home_navigation)
        navController.graph = navGraph

        navView.setupWithNavController(navController)

        val filter = IntentFilter("LOCATION_UPDATE")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                locationReceiver,
                filter,
                RECEIVER_NOT_EXPORTED
            )
        }else{
            registerReceiver(locationReceiver, filter)
        }
        NetworkUtils.startNetworkCallback(this)

        NetworkUtils.networkStatus.observe(this) { isConnected ->
            if (isConnected) {
                locationViewModel.setError("")
            } else {
                locationViewModel.setError("No Internet Connection")
            }
        }
        startService()
    }

    private fun hasPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startService(){
        if (hasPermissions()) {
            val intent = Intent(this, LocationService::class.java)
            this.startService(intent)
        } else {
            ActivityCompat.requestPermissions(this, permissions, 101)
        }
    }

    private fun stopService(){
        val intent = Intent(this, LocationService::class.java)
        this.stopService(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (!hasPermissions()) {
                Toast.makeText(this, "Require permission for best features", Toast.LENGTH_SHORT).show()
            }
        }
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

    override fun onDestroy() {
        stopService()
        unregisterReceiver(locationReceiver)
        NetworkUtils.stopNetworkCallback(this)
        super.onDestroy()
    }
}