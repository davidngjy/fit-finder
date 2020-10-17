package com.sample.fitfinder.domain

enum class BookingStatus(val value: Int) {
    Unknown(0),
    Pending(1),
    Confirmed(2),
    Cancelled(3);

    companion object {
        fun fromInt(value: Int) = values().first {it.value == value}
        fun toInt(value: BookingStatus) = value.value
    }
}