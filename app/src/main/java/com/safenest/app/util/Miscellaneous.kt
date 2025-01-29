package com.safenest.app.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class Miscellaneous(private val context: Context) {

    fun getBatteryPercentage(): String{
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val batteryPct: Int? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale
        }
        return batteryPct.toString()
    }
}