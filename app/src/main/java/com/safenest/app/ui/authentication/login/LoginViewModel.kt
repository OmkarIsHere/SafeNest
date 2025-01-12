package com.safenest.app.ui.authentication.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.ResultState
import com.safenest.app.util.Extension
import com.safenest.app.util.SharedPrefManager

class LoginViewModel(private val firestore : FirebaseFirestore, private val sharedPrefManager: SharedPrefManager) : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String> get() = _userId

//    private val _nestId = MutableLiveData<String>()
//    val nestId: LiveData<String> get() = _nestId

    private val _authStatus = MutableLiveData<ResultState>()
    val authStatus: LiveData<ResultState> get() = _authStatus

    fun getDataFromPreference(key: String, value: String) : String{
        val str = sharedPrefManager.getString(key, value)
        if(key == AppConstant.userId) _userId.value = str
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
                        setDataToPreference(AppConstant.userId, document.getString("id") ?: "")
                        setDataToPreference(AppConstant.userFirstName, document.getString("firstName") ?: "")
                        setDataToPreference(AppConstant.userLastName, document.getString("lastName") ?: "")
                        setDataToPreference(AppConstant.userEmail, document.getString("email") ?: "")
                        setDataToPreference(AppConstant.userPhone, document.getString("phone") ?: "")
                        setDataToPreference(AppConstant.userNest, document.getString("nestId") ?: "")
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