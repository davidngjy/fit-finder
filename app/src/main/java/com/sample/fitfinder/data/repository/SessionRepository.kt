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

// TODO: Remove singleton class
@ExperimentalTime
object SessionRepository {

    private val sessions = arrayListOf(
        Session(1, 1, "Session 1", "My first session", Instant.now(), "1234", LatLng(12.0,12.0), "Melbourne, 3000", true, true, 11, 10),
        Session(2, 1, "Session 2", "My second session", Instant.now(), "5678", LatLng(12.0,12.0), "Mont Albert, 3128", false, true, 22, 25),
        Session(3, 1, "Session 3", "My third session", Instant.now(), "0000", LatLng(12.0,12.0), "South Bank, 3001", true, false, 33, 30),
    )

    private val bookingSession = arrayListOf(
        BookingSession(1, 1, "Booked session 1", "Fully booked session", Instant.now(), LatLng(12.0,12.0), true, true, BigDecimal(11.1), 10.minutes, 1, 1, Instant.now(), BookingStatus.Cancelled)
    )

    fun getSessions() : LiveData<List<Session>> {
        return MutableLiveData<List<Session>>().apply {
            value = sessions
        }
    }

    fun getBookingSessions(): LiveData<List<BookingSession>> {
        return MutableLiveData<List<BookingSession>>().apply {
            value = bookingSession
        }
    }

    fun addSession(newSession: Session) {
        sessions.add(newSession)
    }
}