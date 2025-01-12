package com.safenest.app.model

sealed class ResultState {
    data class Success(val successMessage: String) : ResultState()
    data class Failure(val errorMessage: String) : ResultState()
}