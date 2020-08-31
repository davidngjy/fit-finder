package com.sample.fitfinder.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.domain.BookingSession
import com.sample.fitfinder.domain.BookingStatus
import com.sample.fitfinder.domain.Session
import java.math.BigDecimal
import java.time.Instant
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class SessionRepository {

    @ExperimentalTime
    fun getSessions() : LiveData<List<Session>> {
        return MutableLiveData<List<Session>>().apply {
            value = arrayListOf(
                Session(1, 1, "Session 1", "My first session", Instant.now(), LatLng(12.0,12.0), true, true, BigDecimal(11.1), 10.minutes),
                Session(2, 1, "Session 2", "My second session", Instant.now(), LatLng(12.0,12.0), false, true, BigDecimal(22.1), 25.minutes),
                Session(3, 1, "Session 3", "My third session", Instant.now(), LatLng(12.0,12.0), true, false, BigDecimal(33.1), 30.minutes),
                )
        }
    }

    @ExperimentalTime
    fun getBookingSessions(): LiveData<List<BookingSession>> {
        return MutableLiveData<List<BookingSession>>().apply {
            value = arrayListOf(
                BookingSession(1, 1, "Session 1", "My first session", Instant.now(), LatLng(12.0,12.0), true, true, BigDecimal(11.1), 10.minutes, 1, 1, Instant.now(), BookingStatus.Cancelled)
                )
        }
    }
}