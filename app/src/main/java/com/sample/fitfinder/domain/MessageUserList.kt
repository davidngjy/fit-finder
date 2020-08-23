package com.sample.fitfinder.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.Instant

@Parcelize
data class MessageUserList (
    val id: Long,
    val senderUserId: Long,
    val senderDisplayName: String,
    val senderProfilePicture: String?,
    val latestMessage: String,
    val sentDateTime: Instant
) : Parcelable