package com.safenest.app.repository

import com.safenest.app.model.FcmMessage
import com.safenest.app.model.FcmResponse
import com.safenest.app.model.Response
import com.safenest.app.service.ApiService
import java.io.IOException

interface NotificationRepository  {
    suspend fun sendNotification(authToken: String, fcmMessage : FcmMessage): Response<FcmResponse>
}

class NotificationRepositoryImpl(private val apiService: ApiService) : NotificationRepository{
    override suspend fun sendNotification(authToken: String, fcmMessage: FcmMessage): Response<FcmResponse> {
        return try {
            val response = apiService.sendNotification(authToken, fcmMessage)

            if (response.isSuccessful) {
                Response.Success(response.body()!!)
            } else {
                Response.Failure(IOException("Error occurred while sending notification"))
            }
        } catch (e: IOException) {
            Response.Failure(e)
        } catch (e: Exception) {
            Response.Failure(IOException("Error occurred while communicating with server", e))
        }
    }

}