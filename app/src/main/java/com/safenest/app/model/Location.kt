package com.safenest.app.model

data class Location (
    val nestId : String,
    val member : Member
)

data class Member(
    val userId : String,
    val userName : String,
    val userLatLng : String,
    val dateTime : String
)