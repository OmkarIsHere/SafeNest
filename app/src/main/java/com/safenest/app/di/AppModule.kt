package com.safenest.app.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.safenest.app.repository.AuthRepository
import com.safenest.app.repository.AuthRepositoryImpl
import com.safenest.app.ui.authentication.signup.SignupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single{ FirebaseAuth.getInstance() }

    viewModel { SignupViewModel(get()) }
}