package com.safenest.app.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.safenest.app.util.manager.NotifyWorker
import org.koin.core.Koin

class KoinWorkerFactory(koin: Koin) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            NotifyWorker::class.java.name -> NotifyWorker(appContext, workerParameters)
            else -> null
        }
    }
}
