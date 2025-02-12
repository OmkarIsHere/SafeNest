package com.safenest.app.di

import android.content.Context
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.safenest.app.constant.AppConstant
import com.safenest.app.repository.MapRepository
import com.safenest.app.repository.MapRepositoryImpl
import com.safenest.app.repository.NotificationRepository
import com.safenest.app.repository.NotificationRepositoryImpl
import com.safenest.app.service.ApiService
import com.safenest.app.service.NotificationService
import com.safenest.app.ui.authentication.login.LoginViewModel
import com.safenest.app.ui.authentication.signup.SignupViewModel
import com.safenest.app.ui.location.LocationViewModel
import com.safenest.app.ui.nest.NestViewModel
import com.safenest.app.ui.nest.upload_image.UploadImageViewModel
import com.safenest.app.ui.profile.ProfileViewModel
import com.safenest.app.ui.your_nest.YourNestViewModel
import com.safenest.app.util.manager.LiveDataManager
import com.safenest.app.util.manager.SharedPrefManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single{ FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseMessaging.getInstance() }

    single { provideSharedPreferences(androidContext()) }
    single { SharedPrefManager(get()) }
    single { LiveDataManager(get(), get()) }
    single { NotificationService() }

    single {
        GsonBuilder().setLenient().create()
    }

    single<ApiService> {
        Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(ApiService::class.java)
    }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<MapRepository> { MapRepositoryImpl(get()) }

    viewModel { SignupViewModel(get(), get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { NestViewModel(get(), get()) }
    viewModel { YourNestViewModel(get(), get()) }
    viewModel { UploadImageViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { LocationViewModel(get(), get(), get()) }

}

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(AppConstant.PREF_NAME, Context.MODE_PRIVATE)
}