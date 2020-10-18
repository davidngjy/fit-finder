package com.sample.fitfinder.data.repository

import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.data.database.SessionDao
import com.sample.fitfinder.data.database.SessionEntity
import com.sample.fitfinder.data.gateway.SessionGateway
import com.sample.fitfinder.domain.BookingStatus
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.proto.Response
import com.sample.fitfinder.proto.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

@Singleton
@ExperimentalTime
class SessionRepository @Inject constructor() {

    @Inject lateinit var sessionGateway: SessionGateway
    @Inject lateinit var sessionDao: SessionDao
    @Inject lateinit var currentUserRepository: CurrentUserRepository

    fun getAvailableSessions(): Flow<List<Session>> {
        return sessionDao.getAvailableSessions()
    }

    fun getSession(sessionId: Long): Flow<Session> {
        return sessionDao.getSession(sessionId)
    }

    suspend fun upsertSession(session: Session): Response {
        return if (session.sessionId == 0L) sessionGateway.addSession(session)
        else sessionGateway.editSession(session)
    }

    @ExperimentalCoroutinesApi
    fun getNonBookSessions(): Flow<List<Session>> {
        return currentUserRepository
            .userId
            .flatMapLatest {
                sessionDao.getSessions(BookingStatus.Unknown, it)
            }
    }

    fun getBookedSessions(userId: Long): Flow<List<Session>> {
        return sessionDao.getSessions(BookingStatus.Confirmed, userId)
    }

    suspend fun subscribeToUserSession() {
        updateAllUserSession()
        sessionGateway.subscribeToUserSession()
            .collect {
                updateDatabase(it)
            }
    }

    private suspend fun updateAllUserSession() {
        sessionGateway.getUserSessions()
            .collect {
                updateDatabase(it)
            }
    }

    private suspend fun updateDatabase(session: UserSession) {
        sessionDao.insertOrUpdate(SessionEntity(
            session.sessionId,
            session.trainerUserId,
            session.title,
            session.description,
            Instant.ofEpochSecond(session.sessionDateTime.seconds),
            LatLng(session.location.latitude, session.location.longitude),
            session.locationString,
            session.isOnline,
            session.isInPerson,
            session.price,
            (session.duration.seconds/60).toInt(), // Store per min
            session.bookingId.value,
            session.clientUserId.value,
            BookingStatus.fromInt(session.bookingStatus.number)
        ))
    }
}