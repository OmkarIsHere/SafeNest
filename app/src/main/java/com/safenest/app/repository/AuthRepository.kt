package com.safenest.app.repository

import com.safenest.app.model.Response
import com.safenest.app.model.User

typealias UpdateBookResponse = Response<Void>


interface AuthRepository {
    suspend fun addUser(user : User): Response<UpdateBookResponse>
}

class AuthRepositoryImpl() : AuthRepository{
    override suspend fun addUser(user: User): Response<UpdateBookResponse> {
        TODO("Not yet implemented")
    }


}