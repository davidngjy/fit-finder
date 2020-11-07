package com.sample.fitfinder.domain

import java.time.Instant

data class SessionFilter(
    val maxPrice: Int,
    val online: Boolean,
    val inPerson: Boolean,
    val upperDuration: Int,
    val lowerDuration: Int,
    val upperDateTime: Instant,
    val lowerDateTime: Instant
)