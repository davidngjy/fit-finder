package com.sample.fitfinder.domain

import com.google.android.gms.maps.model.LatLng
import java.math.BigDecimal
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class BookingSession @ExperimentalTime constructor(
    val sessionId: Long,
    val trainerUserId: Long,
    val title: String,
    val description: String,
    val sessionDateTime: Instant,
    val locationSuburb: LatLng,
    val isOnline: Boolean,
    val isInPerson: Boolean,
    val price: BigDecimal,
    val durationInMin: Duration,
    val bookingId: Long,
    val clientUserId: Long,
    val created: Instant,
    val bookingStatusId: BookingStatus
)