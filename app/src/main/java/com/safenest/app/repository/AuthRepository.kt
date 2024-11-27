package com.safenest.app.repository

import com.safenest.app.model.Response
import com.safenest.app.model.User
import com.safenest.app.ui.PermissionActivity
import java.util.concurrent.TimeUnit

typealias BookListResponse = Response<List<User>>
typealias AddBookResponse = Response<String>
typealias UpdateBookResponse = Response<Void>

const val AUTHOR = "author"
const val TITLE = "title"

interface AuthRepository {
    suspend fun addUser(user : User): Response<UpdateBookResponse>
}

class AuthRepositoryImpl() : AuthRepository{
    override suspend fun addUser(user: User): Response<UpdateBookResponse> {
        TODO("Not yet implemented")
    }


}