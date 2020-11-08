package com.sample.fitfinder.data.repository

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.sample.fitfinder.data.database.SessionDao
import com.sample.fitfinder.data.database.SessionEntity
import com.sample.fitfinder.data.gateway.SessionGateway
import com.sample.fitfinder.domain.BookingStatus
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.proto.Region
import com.sample.fitfinder.proto.Response
import com.sample.fitfinder.proto.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor() {

    @Inject lateinit var sessionGateway: SessionGateway
    @Inject lateinit var sessionDao: SessionDao
    @Inject lateinit var currentUserRepository: CurrentUserRepository

    suspend fun getAvailableSessionByMapBound(bounds: LatLngBounds): Flow<List<Session>> {
        return sessionGateway.getSessionsByRegion(
            Region.newBuilder()
                .setNorthEastBound(com.sample.fitfinder.proto.LatLng.newBuilder()
                    .setLatitude(bounds.northeast.latitude)
                    .setLongitude(bounds.northeast.longitude)
                    .build())
                .setSouthWestBound(com.sample.fitfinder.proto.LatLng.newBuilder()
                    .setLatitude(bounds.southwest.latitude)
                    .setLongitude(bounds.southwest.longitude)
                    .build())
                .build())
            .map {
                it.sessionsList.map { session ->
                    Session(
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
                    )
                }
            }
    }

    suspend fun getSession(sessionId: Long): Flow<Session> {
        if (!sessionDao.sessionExists(sessionId)) {
            val session = sessionGateway.getSession(sessionId)
            updateDatabase(session)
        }
        return sessionDao.getSession(sessionId)
    }

    suspend fun upsertSession(session: Session): Response {
        return if (session.sessionId == 0L) sessionGateway.addSession(session)
        else sessionGateway.editSession(session)
    }

    @ExperimentalCoroutinesApi
    fun getMyAvailableSessions(): Flow<List<Session>> {
        return currentUserRepository
            .userId
            .flatMapLatest {
                sessionDao.getAvailableSessions(it)
            }
    }

    fun getUpcomingSessions(): Flow<List<Session>> {
        return sessionDao.getUpcomingSessions()
    }

    fun getPastSessions(): Flow<List<Session>> {
        return sessionDao.getPastSessions()
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

    suspend fun addBooking(sessionId: Long): Response {
        return sessionGateway.addBooking(sessionId)
    }

    suspend fun clearAllSession() {
        sessionDao.clearAllSession()
    }
}