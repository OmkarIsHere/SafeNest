package com.safenest.app.util.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ActivityRecognition", "ActivityTransitionReceiver: Received broadcast for activity transition")

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent!!)

            result?.let {
                for (event in result.transitionEvents) {
                    val activityType = getActivityType(event.activityType)
                    val transitionType =
                        if (event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                            "Started"
                        else
                            "Stopped"

                    Log.d("ActivityRecognition", "ActivityTransitionReceiver : $activityType: $transitionType")
                }
            }
        }else{
            Log.e("ActivityRecognition", "ActivityTransitionReceiver : No activity transition result found in intent")
        }
    }

    private fun getActivityType(activityType: Int): String {
        return when (activityType) {
            DetectedActivity.IN_VEHICLE -> "In Vehicle"
            DetectedActivity.ON_BICYCLE -> "On Bicycle"
            DetectedActivity.ON_FOOT -> "On Foot"
            DetectedActivity.RUNNING -> "Running"
            DetectedActivity.STILL -> "Still"
            DetectedActivity.WALKING -> "Walking"
            DetectedActivity.TILTING -> "Tilting"
            DetectedActivity.UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
    }
}
