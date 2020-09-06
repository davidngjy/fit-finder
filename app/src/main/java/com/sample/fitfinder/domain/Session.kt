package com.sample.fitfinder.domain

import com.google.android.gms.maps.model.LatLng
import java.time.Instant

data class Session(
    val id: Long,
    val trainerUserId: Long,
    val title: String,
    val description: String,
    val sessionDateTime: Instant,
    val locationId: String,
    val locationCoordinate: LatLng,
    val locationSuburb: String,
    val isOnline: Boolean,
    val isInPerson: Boolean,
    val cost: Int,
    val durationInMin: Int
)