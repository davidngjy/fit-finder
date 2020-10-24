package com.sample.fitfinder.data.repository

import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.data.database.SessionDao
import com.sample.fitfinder.data.database.SessionEntity
import com.sample.fitfinder.data.gateway.SessionGateway
import com.sample.fitfinder.data.gateway.UserGateway
import com.sample.fitfinder.domain.BookingStatus
import com.sample.fitfinder.proto.UserSession
import kotlinx.coroutines.flow.collect
import java.time.Instant
import javax.inject.Inject

class SubscriptionRepository @Inject constructor() {

    @Inject lateinit var userGateway: UserGateway
    @Inject lateinit var sessionGateway: SessionGateway
    @Inject lateinit var currentUserRepository: CurrentUserRepository
    @Inject lateinit var sessionDao: SessionDao

    suspend fun subscribeToProfileUpdate() {
        userGateway.subscribeToUserProfile()
            .collect {
                currentUserRepository.upsertCurrentUser(it)
            }
    }

    suspend fun subscribeToUserSession() {
        sessionGateway.subscribeToUserSession()
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