package com.safenest.app.di

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SafeNestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val koin = startKoin {
            androidContext(this@SafeNestApp)
            modules(appModule)
        }.koin

        val config = Configuration.Builder().setWorkerFactory(KoinWorkerFactory(koin)).build()
        WorkManager.initialize(this, config)
    }
}