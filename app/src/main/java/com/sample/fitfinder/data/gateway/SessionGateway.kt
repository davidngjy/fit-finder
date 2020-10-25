package com.sample.fitfinder.data.gateway

import com.google.protobuf.Duration
import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import com.sample.fitfinder.data.repository.GoogleTokenRepository
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.proto.*
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SessionGateway @Inject constructor() {

    @Inject lateinit var channel: ManagedChannel
    @Inject lateinit var googleTokenRepository: GoogleTokenRepository

    private val key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

    suspend fun getSessionsByRegion(region: Region): Flow<UserSessions> {
        val stub = createCoroutineStub()
        return stub.getAvailableSessionsByRegion(region)
    }

    suspend fun getSession(sessionId: Long): UserSession {
        return withContext(Dispatchers.IO) {
            createCoroutineStub().getSession(
                SessionRequest.newBuilder().setSessionId(sessionId).build())
        }
    }

    suspend fun addSession(newSession: Session): Response {
        val stub = createCoroutineStub()
        val request = AddSessionRequest.newBuilder()
            .setTitle(newSession.title)
            .setDescription(newSession.description)
            .setSessionDateTime(Timestamp.newBuilder()
                .setSeconds(newSession.sessionDateTime.epochSecond))
            .setLocation(LatLng.newBuilder()
                .setLatitude(newSession.location.latitude)
                .setLongitude(newSession.location.longitude))
            .setLocationString(newSession.locationString)
            .setIsOnline(newSession.isOnline)
            .setIsInPerson(newSession.isInPerson)
            .setPrice(newSession.price)
            .setDuration(Duration.newBuilder().setSeconds((newSession.duration * 60).toLong()))
            .build()
        return stub.addSession(request)
    }

    suspend fun editSession(editedSession: Session): Response {
        val stub = createCoroutineStub()
        val request = EditSessionRequest.newBuilder()
            .setTrainerUserId(editedSession.trainerUserId)
            .setSessionId(editedSession.sessionId)
            .setTitle(editedSession.title)
            .setDescription(editedSession.description)
            .setSessionDateTime(Timestamp.newBuilder()
                .setSeconds(editedSession.sessionDateTime.epochSecond))
            .setLocation(LatLng.newBuilder()
                .setLatitude(editedSession.location.latitude)
                .setLongitude(editedSession.location.longitude))
            .setLocationString(editedSession.locationString)
            .setIsOnline(editedSession.isOnline)
            .setIsInPerson(editedSession.isInPerson)
            .setPrice(editedSession.price)
            .setDuration(Duration.newBuilder().setSeconds((editedSession.duration * 60).toLong()))
            .build()
        return stub.editSession(request)
    }

    suspend fun addBooking(sessionId: Long): Response {
        return createCoroutineStub()
            .bookSession(BookSessionRequest
                .newBuilder()
                .setSessionId(sessionId)
                .build())
    }

    suspend fun subscribeToUserSession(): Flow<UserSession> {
        val stub = createCoroutineStub()
        return stub.subscribeToUserSession(Empty.getDefaultInstance())
    }

    suspend fun subscribeToBookingSession(): Flow<UserSession> {
        return createCoroutineStub()
            .subscribeToSessionBooking(Empty.getDefaultInstance())
    }

    private suspend fun createCoroutineStub(): SessionProtocolGrpcKt.SessionProtocolCoroutineStub {
        val header = Metadata()
        header.put(key, "Bearer ${googleTokenRepository.getGoogleTokenId()}")
        val stub = SessionProtocolGrpcKt.SessionProtocolCoroutineStub(channel)
        return MetadataUtils.attachHeaders(stub, header)
    }
}