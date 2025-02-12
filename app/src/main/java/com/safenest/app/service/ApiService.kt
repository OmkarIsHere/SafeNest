package com.safenest.app.service

import com.safenest.app.constant.AppConstant
import com.safenest.app.model.FcmMessage
import com.safenest.app.model.FcmResponse
import com.safenest.app.model.FullAddress
import org.koin.core.component.KoinComponent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService: KoinComponent {

    @Headers("Content-Type: application/json")
    @POST(AppConstant.NOTIFICATION_URL)
    suspend fun sendNotification(
        @Header("Authorization") authToken: String,
        @Body payload: FcmMessage
    ): Response<FcmResponse>

    @GET
    suspend fun getFullAddress(
        @Url mapUrl : String,
        @Query("at") at : String,
        @Query("apiKey") apiKey : String,
    ): Response<FullAddress>
}