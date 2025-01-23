package com.safenest.app.model

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("status"  ) var status  : String? = null,
    @SerializedName("message" ) var message : String? = null
)