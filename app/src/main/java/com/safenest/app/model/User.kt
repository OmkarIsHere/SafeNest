package com.safenest.app.model

data class User (
    var id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var password: String? = null,
    var nestId: String? = null
)