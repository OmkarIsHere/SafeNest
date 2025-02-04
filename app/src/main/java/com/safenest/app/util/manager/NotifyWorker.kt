package com.safenest.app.util.manager

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.content.pm.PackageManager
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.safenest.app.R
import com.safenest.app.constant.AppConstant
import com.safenest.app.service.NotificationService
import com.safenest.app.ui.MainActivity

class NotifyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            getCurrentLocation(applicationContext)
            Log.d("DOWORK", "5")
            return Result.success()
        }catch (e : Exception){
            Log.e("DOWORK", "Exception: ${e.message}")
            return Result.failure()
        }
    }

    private fun getCurrentLocation(context: Context){
        val fusedLocationClient: FusedLocationProviderClient
        if(checkLocationPermission(context = context)){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val notificationService = NotificationService()
//                    val notificationId: String = inputData.getString(AppConstant.N_ID)!!
//                    val notificationName: String = inputData.getString(AppConstant.N_NAME)!!
                    val id = 3 //inputData.getInt(AppConstant.ID, 3)
                    val title = "Hello World" //inputData.getString(AppConstant.TITLE)!!
                    val body = "Body of hello world" //inputData.getString(AppConstant.BODY)!!

                    Log.d("DOWORK", "getCurrentLocation: Lat: $latitude, Lng: $longitude")
                    showNotification()
                } else {
                    Log.e("DOWORK", "Location not available")
                }
            }.addOnFailureListener {
                Log.e("DOWORK", "Failed to get location")
            }
        }
    }

    private fun checkLocationPermission(context : Context): Boolean {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showNotification() {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0, intent, PendingIntent.FLAG_IMMUTABLE
        )


        val notification = NotificationCompat.Builder(
            applicationContext,
            AppConstant.N_ID
        )
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("New Task")
            .setContentText("Subscribe on the channel")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelDescription = "Channel Description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(AppConstant.N_ID, AppConstant.N_NAME, channelImportance).apply {
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }


        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(3, notification.build())
        }

    }

}