package com.safenest.app.ui.location

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.safenest.app.constant.AppConstant
import com.safenest.app.model.Member
import com.safenest.app.util.manager.LiveDataManager
import com.safenest.app.util.Miscellaneous
import com.safenest.app.util.manager.SharedPrefManager
import com.safenest.app.util.manager.NotifyWorker
import java.util.concurrent.TimeUnit

class LocationViewModel(private val liveDataManager: LiveDataManager, private val sharedPrefManager: SharedPrefManager) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _members = MutableLiveData<List<Member>>()
    val members: LiveData<List<Member>> get() = _members

    private val _isNotifyEnqueue = MutableLiveData<Boolean>()
    val isNotifyEnqueue: LiveData<Boolean> get() = _isNotifyEnqueue

    fun setError(str: String){
        _error.value = str
    }

    fun updateDatabase(context: Context, lat: String, lng: String){
        val battery = Miscellaneous(context).getBatteryPercentage()
        liveDataManager.setData(lat, lng, battery)
    }

    fun isNotifyWorkerStarted(context:Context){
        val workManager = WorkManager.getInstance(context)
        workManager.getWorkInfosByTagLiveData(AppConstant.DO_WORK).observeForever { workInfoList ->
            _isNotifyEnqueue.value = workInfoList.any { it.state == WorkInfo.State.ENQUEUED }

        }
    }

    fun readDatabase(){
        val locationRef = liveDataManager.getData()
        val userId = sharedPrefManager.getString(AppConstant.USERID, "")
        locationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val locations = mutableListOf<Member>()
                for (data in dataSnapshot.children) {
                    val map = data.value as? Map<*, *> ?: continue
                    val userLocation = Member(
                        userId = data.key ?: "",
                        userName = map["userName"] as? String ?: "",
                        userLatLng = map["userLatLng"] as? String ?: "",
                        dateTime = map["dateTime"] as? String ?: "",
                        battery = map["battery"] as? String ?: "",
                        userPhone = map["userPhone"] as? String ?: "",
                        internet = map["internet"] as? String ?: "",
                        userIcon = map["userIcon"] as? String ?: ""
                    )
                    if(userId != data.key){
                        locations.add(userLocation)
                    }
                }
                _members.value = locations
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LocationService", "Failed to read value.", error.toException())
            }
        })
    }

    fun notifyWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        try {
            if (_isNotifyEnqueue.value == true) {
                workManager.cancelAllWorkByTag(AppConstant.DO_WORK)
                Log.d("DOWORK", "notifyWork: cancelAllWorkByTag")
            } else {
                val constraints: Constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresCharging(false)
                    .setRequiresBatteryNotLow(true)
                    .build()

                val myWorkRequest = PeriodicWorkRequest.Builder(
                    NotifyWorker::class.java,
                    15,
                    TimeUnit.MINUTES
                ).setConstraints(constraints)
                    .addTag(AppConstant.DO_WORK)
                    .build()

                workManager.enqueueUniquePeriodicWork(
                    AppConstant.DO_WORK,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    myWorkRequest
                )
            }
        }catch (e : Exception){
            Log.e("DOWORK", "notifyWork: ${e.stackTrace}")
        }
    }
}