package com.sample.fitfinder.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.domain.BookingSession
import com.sample.fitfinder.domain.BookingStatus
import com.sample.fitfinder.domain.Session
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@Singleton
@ExperimentalTime
class SessionRepository @Inject constructor() {

    private val sessions = arrayListOf(
        Session(1, 1, "Session 1", "My first session", Instant.now(), LatLng(-37.809566, 145.078613), "Balwyn, 3103, Victoria", true, true, 11, 10),
        Session(2, 1, "Session 2", "My second session", Instant.now(), LatLng(-37.842360, 145.016557), "Toorak, 3142, Victoria", false, true, 22, 25),
        Session(3, 1, "Session 3", "My third session", Instant.now(), LatLng(-37.786491, 144.836742), "Sunshine, 3020, Victoria", true, false, 33, 30),
    )

    private val bookingSession = arrayListOf(
        BookingSession(1, 1, "Booked session 1", "Fully booked session", Instant.now(), LatLng(12.0,12.0), true, true, BigDecimal(11.1), 10.minutes, 1, 1, Instant.now(), BookingStatus.Cancelled)
    )

    fun getSessions() : LiveData<List<Session>> {
        return MutableLiveData<List<Session>>().apply {
            value = sessions
        }
    }

    fun getSession(id: Long) : LiveData<Session> {
        return MutableLiveData<Session>().apply {
            value = sessions.first { it.id == id }
        }
    }

    fun getBookingSessions(): LiveData<List<BookingSession>> {
        return MutableLiveData<List<BookingSession>>().apply {
            value = bookingSession
        }
    }

    fun addSession(newSession: Session) {
        val assignedId = newSession.copy(id = sessions.count().toLong())
        sessions.add(assignedId)
    }

    fun updateSession(sessionId: Long, updatedSession: Session) {
        sessions[sessionId.toInt() - 1] = updatedSession
    }
}