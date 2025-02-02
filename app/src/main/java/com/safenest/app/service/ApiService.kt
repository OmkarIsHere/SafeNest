package com.safenest.app.service

import com.safenest.app.constant.AppConstant
import com.safenest.app.model.FcmMessage
import com.safenest.app.model.FcmResponse
import org.koin.core.component.KoinComponent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService: KoinComponent {

    @Headers("Content-Type: application/json")
    @POST(AppConstant.NOTIFICATION_URL)
    suspend fun sendNotification(
        @Header("Authorization") authToken: String,
        @Body payload: FcmMessage
    ): Response<FcmResponse>
}