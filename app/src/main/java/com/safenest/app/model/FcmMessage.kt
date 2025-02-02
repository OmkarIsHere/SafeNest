package com.safenest.app.model

data class FcmMessage(
    val message: MessageData
)

data class MessageData(
    val topic: String,
    val notification: NotificationData
)

data class NotificationData(
    val title: String,
    val body: String
)

data class FcmResponse(
    val name: String
)
