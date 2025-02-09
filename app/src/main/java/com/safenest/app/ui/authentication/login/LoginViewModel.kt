package com.safenest.app.ui.authentication.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.ResultState
import com.safenest.app.service.NotificationService
import com.safenest.app.util.Extension
import com.safenest.app.util.manager.SharedPrefManager

class LoginViewModel(
    private val firestore : FirebaseFirestore,
    private val notificationService: NotificationService,
    private val sharedPrefManager: SharedPrefManager
) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

    private val _authStatus = MutableLiveData<ResultState>()
    val authStatus: LiveData<ResultState> get() = _authStatus

    fun getDataFromPreference(key: String, value: String) : String{
        val str = sharedPrefManager.getString(key, value)
        if(key == AppConstant.USERID) _userId.value = str
        return str
    }

    private fun isValidCredentials(email: String, password: String): Boolean {
        return Extension.isStringNotEmpty(email) && Extension.isPasswordValid(password)
    }

    private fun getValidationErrorMessage(userEmail: String, userPassword: String): String {

        if(Extension.isStringEmpty(userEmail))
            return "Please enter a email address"

        if(!Extension.isEmailValid(userEmail))
            return "Please enter a valid email address"

        if(Extension.isStringEmpty(userPassword))
            return "Please enter a password"

        if(!Extension.isPasswordValid(userPassword))
            return "Password at least of 8 characters-\nOne uppercase character\nOne lowercase character\nOne Special character\nOne Numeric character"

        return ""
    }

    fun login(email: String, password:String){
        if(isValidCredentials(email, password)){
            loginUser(email, password)
        }else{
            _authStatus.postValue(ResultState.Failure(getValidationErrorMessage(email, password)))
        }
    }

    private fun loginUser(email: String, password: String){
        val userCollection = firestore.collection(AppConstant.USER)
        userCollection.whereEqualTo("email", email)
            .whereEqualTo("password", Extension.hashPassword(password))
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        setDataToPreference(AppConstant.USERID, document.getString("id") ?: "")
                        setDataToPreference(AppConstant.USER_FIRSTNAME, document.getString("firstName") ?: "")
                        setDataToPreference(AppConstant.USER_LASTNAME, document.getString("lastName") ?: "")
                        setDataToPreference(AppConstant.USER_EMAIL, document.getString("email") ?: "")
                        setDataToPreference(AppConstant.USER_PHONE, document.getString("phone") ?: "")
                        setDataToPreference(AppConstant.USER_NEST, document.getString("nestId") ?: "")
                        setDataToPreference(AppConstant.USER_ICON, document.getString("userIcon") ?: "")
                        notificationService.subscribeToTopic(document.getString("nestId") ?: "")
                    }
                    _authStatus.postValue(ResultState.Success("Logged in successfully"))
                } else {
                    _authStatus.postValue(ResultState.Failure("Invalid email or password"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.message!!)
                _authStatus.postValue(ResultState.Failure(exception.message?: "Something went wrong"))
            }
    }

    private fun setDataToPreference(key: String, value: String){
        sharedPrefManager.putString(key, value)
    }
}