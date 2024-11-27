package com.safenest.app.model

data class Nest(
    val nId: Int = 0,
    val nName: String = "",
    val nMembers: Map<String, String> = emptyMap()
)