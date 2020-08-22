package com.sample.fitfinder.domain

import java.time.Instant

data class MessageUserList (
    val id: Long,
    val senderUserId: Long,
    val senderDisplayName: String,
    val senderProfilePicture: String?,
    val latestMessage: String,
    val sentDateTime: Instant
)