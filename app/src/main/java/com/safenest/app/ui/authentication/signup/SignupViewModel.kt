package com.safenest.app.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.util.Extension
import com.safenest.app.model.User
import com.safenest.app.service.NotificationService
import com.safenest.app.util.SharedPrefManager
import java.util.concurrent.TimeUnit

class SignupViewModel(
    private val auth : FirebaseAuth,
    private val firestore : FirebaseFirestore,
    private val notificationService: NotificationService,
    private val sharedPrefManager: SharedPrefManager) : ViewModel() {

    private val TAG = "SignupViewModel"

    private lateinit var user:User

    private val _verificationId = MutableLiveData<String>()

    private val _authStatus = MutableLiveData<AuthStatus>()
    val authStatus: LiveData<AuthStatus> get() = _authStatus

    private fun isValidCredentials(fName: String, lName: String, phone: String, email: String, password: String, cnfPassword: String): Boolean {
        return Extension.isStringNotEmpty(fName) && Extension.isStringNotEmpty(lName) && Extension.isStringNotEmpty(email) && Extension.isStringNotEmpty(phone)
                && Extension.isPasswordValid(password) && Extension.isStringNotEmpty(cnfPassword) && Extension.compareTwoString(password, cnfPassword)
    }

    private fun getValidationErrorMessage(fName: String, lName: String, userPhone: String, userEmail: String, userPassword: String, userCnfPassword: String): String {

        if(Extension.isStringEmpty(fName))
            return "Please enter a first name"

        if(Extension.isStringEmpty(lName))
            return "Please enter a last name"

        if(Extension.isStringEmpty(userPhone))
            return "Please enter phone number"

        if(!Extension.isMobileValid(userPhone))
            return "Please enter valid phone number"

        if(Extension.isStringEmpty(userEmail))
            return "Please enter a email address"

        if(!Extension.isEmailValid(userEmail))
            return "Please enter a valid email address"

        if(Extension.isStringEmpty(userPassword))
            return "Please enter a password"

        if(!Extension.isPasswordValid(userPassword))
            return "Password at least of 8 characters-\nOne uppercase character\nOne lowercase character\nOne Special character\nOne Numeric character"

        if(!Extension.compareTwoString(userPassword,userCnfPassword))
            return "Both password should be same"

        return ""
    }

    fun signup(fName:String, lName:String, email:String, phone: String, password:String, cnfPassword:String){
        if(isValidCredentials(fName, lName, email, phone, password, cnfPassword)){
            val uPhone = "+91$phone"
            user = User(
                firstName = fName,
                lastName = lName,
                email = email,
                phone = uPhone,
                userIcon = "",
                password = Extension.hashPassword(password)
            )
            phoneAuthentication(uPhone)
        }else{
            _authStatus.postValue(AuthStatus.Failure(getValidationErrorMessage(fName, lName, phone,email, password, cnfPassword), true ))
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "Verification Completed")
        }
        override fun onVerificationFailed(e: FirebaseException) {
            _authStatus.postValue(AuthStatus.Failure(e.message!!, true))
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
            _authStatus.postValue(AuthStatus.CodeSent(verificationId))
            _verificationId.value = verificationId
        }
    }

    private fun phoneAuthentication(phone : String) {
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
                        user = user.copy( id = firebaseUser.uid)
                        signupUser(user)
                    }
                } else {
                    Log.e(TAG, task.exception?.message!!)
                    _authStatus.postValue(AuthStatus.Failure(task.exception?.message ?: "Sign up failed", false))
                }
            }
    }

    private fun signupUser(user: User){
        val userCollection = firestore.collection(AppConstant.USER).document(user.id!!)
        userCollection.set(user)
            .addOnSuccessListener {
                setDataToPreference(AppConstant.USERID, user.id ?: "")
                setDataToPreference(AppConstant.USER_FIRSTNAME, user.firstName ?: "")
                setDataToPreference(AppConstant.USER_FIRSTNAME, user.lastName ?: "")
                setDataToPreference(AppConstant.USER_EMAIL, user.email ?: "")
                setDataToPreference(AppConstant.USER_PHONE, user.phone ?: "")
                setDataToPreference(AppConstant.USER_NEST, user.nestId ?: "")
                notificationService.subscribeToTopic(user.nestId ?: "")
                _authStatus.postValue(AuthStatus.Success("Sign up completed"))
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message!!)
                _authStatus.postValue(AuthStatus.Failure(exception.message ?: "Sign up failed",false))
            }
    }

    private fun setDataToPreference(key: String, value: String){
        sharedPrefManager.putString(key, value)
    }

}

sealed class AuthStatus {
    data class Success(val successMessage: String) : AuthStatus()
    data class Failure(val errorMessage: String, val isSignup: Boolean) : AuthStatus()
    data class CodeSent(val verificationId: String) : AuthStatus()
}