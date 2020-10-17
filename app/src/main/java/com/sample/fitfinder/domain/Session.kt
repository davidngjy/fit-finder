package com.sample.fitfinder.domain

import com.google.android.gms.maps.model.LatLng
import java.time.Instant

data class Session(
    val sessionId: Long,
    val trainerUserId: Long,
    val title: String,
    val description: String,
    val sessionDateTime: Instant,
    val location: LatLng,
    val locationString: String,
    val isOnline: Boolean,
    val isInPerson: Boolean,
    val price: Double,
    val duration: Int,
    val bookingId: Long? = null,
    val clientUserId: Long? = null,
    val bookingStatus: BookingStatus = BookingStatus.Unknown
)