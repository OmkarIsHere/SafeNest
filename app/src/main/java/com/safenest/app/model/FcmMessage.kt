package com.safenest.app.model

data class FcmMessage(
    val message: MessageData
)

data class MessageData(
    val topic: String,
    val notification: NotificationData,
    val payLoadData: PayLoadData
)

data class NotificationData(
    val title: String,
    val body: String
)

data class PayLoadData(
    val uId: String,
    val notificationId: String,
    val notificationName: String,
    val id: Int
)

data class FcmResponse(
    val name: String
)
