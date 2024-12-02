package com.safenest.app.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.safenest.app.constant.AppConstant
import com.safenest.app.repository.AuthRepository
import com.safenest.app.repository.AuthRepositoryImpl
import com.safenest.app.ui.authentication.login.LoginViewModel
import com.safenest.app.ui.authentication.signup.SignupViewModel
import com.safenest.app.ui.nest.NestViewModel
import com.safenest.app.util.SharedPrefManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single{ FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    viewModel { SignupViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { NestViewModel(get(), get()) }

    single { provideSharedPreferences(androidContext()) }
    single { SharedPrefManager(get()) }
}

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(AppConstant.prefName, Context.MODE_PRIVATE)
}