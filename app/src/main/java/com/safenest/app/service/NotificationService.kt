package com.safenest.app.service

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.safenest.app.constant.AppConstant
import com.safenest.app.util.SharedPrefManager

class NotificationService(private val firebaseMessaging: FirebaseMessaging, private val sharedPrefManager: SharedPrefManager) : FirebaseMessagingService() {
    private val TAG = "NotificationService"

    private val nestId = getNestIdFromSf()

    private fun getNestIdFromSf() : String{
        return sharedPrefManager.getString(AppConstant.userNest, "")
    }

    fun getToken(){
        firebaseMessaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            Log.d(TAG, token)
        })
    }

    fun subscribeToTopic(){
        firebaseMessaging.subscribeToTopic(nestId)
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d(TAG, "subscribeToTopic: $msg")
            }
    }

    fun unSubscribeToTopic(){
        firebaseMessaging.unsubscribeFromTopic(nestId)
            .addOnCompleteListener { task ->
                var msg = "Unsubscribed"
                if (!task.isSuccessful) {
                    msg = "Unsubscribe failed"
                }
                Log.d(TAG, "unsubscribeToTopic: $msg")
            }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG,"onNewToken: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "onMessageReceived: ${remoteMessage.from}")
    }
}