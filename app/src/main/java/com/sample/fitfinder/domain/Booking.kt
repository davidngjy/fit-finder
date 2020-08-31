package com.sample.fitfinder.domain

import java.time.Instant
import kotlin.time.ExperimentalTime

data class Booking @ExperimentalTime constructor(
    val id: Long,
    val sessionId: Long,
    val clientUserId: Long,
    val created: Instant,
    val bookingStatusId: BookingStatus
)