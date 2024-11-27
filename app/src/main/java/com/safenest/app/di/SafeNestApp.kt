package com.safenest.app.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SafeNestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SafeNestApp)
            modules(appModule)
        }
    }
}