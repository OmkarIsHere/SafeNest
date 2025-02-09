package com.safenest.app.service

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.safenest.app.R
import com.safenest.app.constant.AppConstant
import com.safenest.app.util.manager.SharedPrefManager
import org.koin.android.ext.android.inject

class NotificationService() : FirebaseMessagingService() {

    private val TAG = "NotificationService"
    private val firebaseMessaging: FirebaseMessaging by inject()
    private val sharedPrefManager: SharedPrefManager by inject()

    fun subscribeToTopic(nestId : String){
        firebaseMessaging.subscribeToTopic(nestId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "subscribeToTopic: $nestId")
                }
            }
    }

    fun unSubscribeToTopic(nestId : String){
        firebaseMessaging.unsubscribeFromTopic(nestId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "unsubscribeToTopic: $nestId")
                }
            }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG,"onNewToken: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {
            val userId = sharedPrefManager.getString(AppConstant.USERID, "")

            try {
                val uId = remoteMessage.data["uId"] ?: ""
                val notificationId = remoteMessage.data["notificationId"] ?: AppConstant.NOTIFICATION_ID
                val notificationName = remoteMessage.data["notificationName"] ?: AppConstant.NOTIFICATION_NAME
                val id = remoteMessage.data["id"]?.toIntOrNull() ?: 2

/*                if (uId != userId) {
                    showNotification(
                        remoteMessage.notification?.title ?: "SafeNest",
                        remoteMessage.notification?.body ?: "You have a new notification",
                        notificationId,
                        notificationName,
                        id
                    )
                }*/
            } catch (e: Exception) {
                Log.e(TAG, "Error processing FCM message: ${e.localizedMessage}")
            }

//            Log.d(TAG, "onMessageReceived: ${remoteMessage.notification!!.title}")
//
            showNotification(
                remoteMessage.notification?.title?:"",
                remoteMessage.notification?.body?:"",
                AppConstant.NOTIFICATION_ID,
                AppConstant.NOTIFICATION_NAME,
                2)
        }
    }

    private fun showNotification(title:String, body:String, notificationId: String, notificationName: String, id:Int){
        val notification = NotificationCompat.Builder(applicationContext, notificationId)
            .setSmallIcon(R.drawable.icon)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVibrate(longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                ))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setContentText(body)
                .setGroup(title)

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(
                notificationId,
                notificationName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                enableLights(true)
                setSound((sound),audioAttributes)
            }
            notificationManager.createNotificationChannel(
                notificationChannel
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(applicationContext, POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(id, notification.build())
            }
        } else {
            notificationManager.notify(id, notification.build())
        }

    }
}