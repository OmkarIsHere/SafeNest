package com.safenest.app.repository

import com.safenest.app.constant.AppConstant
import com.safenest.app.model.FullAddress
import com.safenest.app.model.Response
import com.safenest.app.service.ApiService
import java.io.IOException

interface MapRepository  {
    suspend fun getFullAddress(latLng: String): Response<FullAddress>
}

class MapRepositoryImpl(private val apiService: ApiService) : MapRepository{
    override suspend fun getFullAddress(latLng: String): Response<FullAddress> {
        return try {
            val response = apiService.getFullAddress(AppConstant.MAP_URL, latLng, AppConstant.HERE_KEY)
            if (response.isSuccessful) {
                Response.Success(response.body()!!)
            } else {
                Response.Failure(IOException("Error occurred while getting address"))
            }
        } catch (e: IOException) {
            Response.Failure(e)
        } catch (e: Exception) {
            Response.Failure(IOException("Error occurred while communicating with server", e))
        }
    }

}