package com.safenest.app.ui.nest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.Nest
import com.safenest.app.util.Extension
import com.safenest.app.util.SharedPrefManager

class NestViewModel(private val firestore : FirebaseFirestore, private val sharedPrefManager: SharedPrefManager) : ViewModel() {
    private val TAG = "NestViewModel"
    lateinit var nest : Nest
    private var uId = ""

    private val nestCollection = firestore.collection(AppConstant.NEST)
    private val userCollection = firestore.collection(AppConstant.USER)

    private val _nestState = MutableLiveData<NestState>()
    val nestState: LiveData<NestState> get() = _nestState

    private val _nId = MutableLiveData<String>()
    val nId: LiveData<String> get() = _nId


    fun createNest(name: String){
        if (Extension.isStringEmpty(name)){
            _nestState.postValue(NestState.Failure("Please enter a nest name"))
            return
        }else{
            uId = getDataFromPreference(AppConstant.userId, "")

            if(checkUserInNestOrNot(uId)){
                _nestState.postValue(NestState.Failure("You are already in a nest"))
                return
            }

            nest = Nest(
                nestName = name,
                nestAdmin = uId,
                nestMembers = listOf(uId)
            )
            try {
                nestCollection.add(nest)
                    .addOnSuccessListener { task ->
                        _nId.value = task.id
                        updateNest(task.id)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, exception.message!!)
                        _nestState.postValue(
                            NestState.Failure(exception.message ?: "Something went wrong")
                        )
                    }
            }catch (e : Exception){
                Log.e(TAG, e.message!!)
                _nestState.postValue(NestState.Failure("Something went wrong, please try again later"))
            }
        }
    }

    fun joinNest(nId: String){
        if (Extension.isStringEmpty(nId)){
            _nestState.postValue(NestState.Failure("Please enter a nest code"))
            return
        }else{
            uId = getDataFromPreference(AppConstant.userId, "")
            addMemberToNest(uId, nId)
        }
    }

    private fun checkUserInNestOrNot(uId: String): Boolean{
        var status = false;
        try {
            userCollection.document(uId)
                .get()
                .addOnSuccessListener { document ->
                    status = if (document != null && document.exists()) {
                        val nestId = document.getString("nestId")
                        nestId.isNullOrEmpty()
                    } else {
                        false
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message!!)
                    _nestState.postValue(NestState.Failure("Something went wrong, please try again later"))
                }
        }catch(e: Exception){
            Log.e(TAG, e.message!!)
        }
        return status
    }

    private fun updateNest(nId: String){
        try {
            nestCollection.document(nId).update("nestId", nId)
                .addOnSuccessListener {
                    Log.d(TAG, "nest id updated")
                    updateUser(nId, false)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message!!)
                    _nestState.postValue(
                        NestState.Failure(exception.message ?: "Something went wrong")
                    )
                }
        }catch (e : Exception){
            Log.e(TAG, e.message!!)
            _nestState.postValue(NestState.Failure("Something went wrong, please try again later"))
        }
    }

    private fun updateUser(nId: String, fromJoinNest: Boolean){
        try {
            userCollection.document(uId).update("nestId", nId)
                .addOnSuccessListener {
                    if(fromJoinNest){
                        _nestState.postValue(NestState.Success("Nest joined successfully"))
                    }else{
                        _nestState.postValue(NestState.Success("Nest created successfully"))
                    }
                    setDataToPreference(AppConstant.userNest, nId)
                    Log.d(TAG, "user's nest id updated")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message!!)
                    _nestState.postValue(
                        NestState.Failure(exception.message ?: "Something went wrong")
                    )
                }
        }catch (e : Exception){
            Log.e(TAG, e.message!!)
            _nestState.postValue(NestState.Failure("Something went wrong, please try again later"))
        }
    }

    private fun addMemberToNest(uId: String, nId:String){
        if(checkUserInNestOrNot(uId)){
            _nestState.postValue(NestState.Failure("You are already in a nest"))
            return
        }
        try {
            nestCollection.document(nId).update("nestMembers", FieldValue.arrayUnion(uId))
                .addOnSuccessListener {
                    Log.d(TAG, "one member added : $uId")
                    updateUser(nId, true)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, exception.message!!)
                    _nestState.postValue(NestState.Failure(exception.message ?: "Something went wrong"))
                }
        }catch (e : Exception){
            Log.e(TAG, e.message!!)
            _nestState.postValue(NestState.Failure("Something went wrong, please try again later"))
        }
    }

    private fun getDataFromPreference(key: String, value: String) : String{
        return sharedPrefManager.getString(key, value)
    }

    private fun setDataToPreference(key: String, value: String){
        sharedPrefManager.putString(key, value)
    }
}

sealed class NestState {
    data class Success(val successMessage: String) : NestState()
    data class Failure(val errorMessage: String) : NestState()
}