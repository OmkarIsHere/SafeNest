package com.safenest.app.model

data class Nest(
    val nestId: String = "",
    val nestName: String = "",
    val nestAdmin: String = "",
    val nestMembers: List<String> = emptyList()
)