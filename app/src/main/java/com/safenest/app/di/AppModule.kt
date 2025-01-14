package com.safenest.app.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.ui.authentication.login.LoginViewModel
import com.safenest.app.ui.authentication.signup.SignupViewModel
import com.safenest.app.ui.location.LocationViewModel
import com.safenest.app.ui.nest.NestViewModel
import com.safenest.app.ui.profile.ProfileViewModel
import com.safenest.app.ui.your_nest.YourNestViewModel
import com.safenest.app.util.LocationManager
import com.safenest.app.util.SharedPrefManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single{ FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseDatabase.getInstance() }

    single { provideSharedPreferences(androidContext()) }
    single { SharedPrefManager(get()) }
    single { LocationManager(get(), get()) }

    viewModel { SignupViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { NestViewModel(get(), get()) }
    viewModel { YourNestViewModel(get(), get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { LocationViewModel(get()) }

}

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(AppConstant.prefName, Context.MODE_PRIVATE)
}