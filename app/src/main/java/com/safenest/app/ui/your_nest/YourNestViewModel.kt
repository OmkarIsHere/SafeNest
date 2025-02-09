package com.safenest.app.ui.your_nest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.ResultState
import com.safenest.app.model.Nest
import com.safenest.app.util.manager.SharedPrefManager

class YourNestViewModel(private val firestore : FirebaseFirestore, private val sharedPrefManager: SharedPrefManager) : ViewModel() {

    private val TAG = "YourNestViewModel"

    private val _nest = MutableLiveData<Nest>()
    val nest: LiveData<Nest> get() = _nest

    private val _resultState = MutableLiveData<ResultState>()
    val resultState: LiveData<ResultState> get() = _resultState

    fun getNestData(){
        val nestId = sharedPrefManager.getString(AppConstant.USER_NEST, "")
        firestore.collection(AppConstant.NEST)
            .document(nestId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val fetchedNest = document.toObject(Nest::class.java)
                    if (fetchedNest != null) {
                        _nest.value = fetchedNest!!
                        _resultState.postValue(ResultState.Success( "Nest details found"))
                        Log.d(TAG, "Nest: $fetchedNest")
                    } else {
                        _resultState.postValue(ResultState.Success( "Error occurred while fetching data"))
                        Log.e(TAG, "Failed to convert document to Nest")
                    }
                }else{
                    _resultState.postValue(ResultState.Failure( "Nest is not exists"))
                }
            }
            .addOnFailureListener { exception ->
                _resultState.postValue(ResultState.Failure( "Something went wrong!!"))
                Log.e(TAG, exception.message!!)
            }
    }

}