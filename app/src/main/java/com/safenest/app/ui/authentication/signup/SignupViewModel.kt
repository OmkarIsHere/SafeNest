package com.safenest.app.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.safenest.app.model.User
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SignupViewModel(private val auth : FirebaseAuth) : ViewModel() {

    private val TAG = "SignupViewModel"

    private val _verificationId = MutableLiveData<String>()

    private val _authStatus = MutableLiveData<AuthState>()
    val authStatus: LiveData<AuthState> get() = _authStatus

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted")
        }
        override fun onVerificationFailed(e: FirebaseException) {
            Log.e(TAG, "onVerificationFailed", e)
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    Log.e(TAG, "FirebaseAuthInvalidCredentialsException", e)
                }

                is FirebaseTooManyRequestsException -> {
                    Log.e(TAG, "FirebaseTooManyRequestsException", e)
                }

                is FirebaseAuthMissingActivityForRecaptchaException -> {
                    Log.e(TAG, "FirebaseAuthMissingActivityForRecaptchaException", e)
                }
            }
        }
        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            _authStatus.postValue(AuthState.CodeSent(verificationId))
            _verificationId.value = verificationId
        }
    }

    fun phoneAuthentication(phone : String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(otpCode: String) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value!!, otpCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.let { firebaseUser ->
                        Log.d(TAG, "Credentials UId: ${firebaseUser.uid}, phoneNumber: ${firebaseUser.phoneNumber}")
                        _authStatus.postValue(AuthState.Success("user"))
                    }

                    _authStatus.postValue(AuthState.Success("Verification Completed"))
                } else {
                    _authStatus.postValue(AuthState.Failure(task.exception?.message ?: "Sign-in failed"))
                }
            }
    }

}

sealed class AuthState {
    data class Success(val successMessage: String) : AuthState()
    data class Failure(val errorMessage: String) : AuthState()
    data class CodeSent(val verificationId: String) : AuthState()
}