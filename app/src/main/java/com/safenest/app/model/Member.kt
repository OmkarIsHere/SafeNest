package com.safenest.app.model

data class Member(
    var userId : String? = "",
    var userName : String? = "",
    var userPhone : String? = "",
    var userLatLng : String? = "",
    var userIcon : String? = "",
    var battery : String? = "",
    var internet : String? = "",
    var dateTime : String? = ""
)