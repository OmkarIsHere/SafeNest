package com.safenest.app.service

import com.safenest.app.model.PostResponse
import org.koin.core.component.KoinComponent
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService: KoinComponent {

    @POST("create_user.php")
    suspend fun createUser(
        @Body body: HashMap<String, Any>
    ): Response<PostResponse>

}