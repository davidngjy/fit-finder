package com.sample.fitfinder.domain

import java.time.Instant

data class Message (
    val id: Long,
    val messageSender: MessageSender,
    val content: String,
    val messageType: MessageType,
    val senderUserId: Long,
    val sentDateTime: Instant
)