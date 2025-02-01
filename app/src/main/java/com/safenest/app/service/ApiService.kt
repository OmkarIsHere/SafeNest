package com.safenest.app.service

import com.safenest.app.constant.AppConstant
import com.safenest.app.model.FcmMessage
import com.safenest.app.model.FcmResponse
import com.safenest.app.model.PostResponse
import org.koin.core.component.KoinComponent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService: KoinComponent {

    @POST("create_user.php")
    suspend fun createUser(
        @Body body: HashMap<String, Any>
    ): Response<PostResponse>

    @Headers("Content-Type: application/json")
    @POST(AppConstant.NOTIFICATION_URL)
    fun sendNotification(
        @Header("Authorization") authToken: String,
        @Body payload: FcmMessage
    ): Response<FcmResponse>
}