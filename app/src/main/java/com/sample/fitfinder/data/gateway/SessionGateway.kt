package com.sample.fitfinder.data.gateway

import com.google.protobuf.Duration
import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import com.sample.fitfinder.data.repository.GoogleTokenRepository
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.proto.*
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
import io.grpc.Metadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.Executor
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
        var currentDelay = 100L
        val delayFactor = 2
        return createCoroutineStub()
            .subscribeToUserSession(Empty.getDefaultInstance())
            .retryWhen { _, attempt ->
                Timber.e("Connection lost on subscribeToUserSession attempt: $attempt")
                delay(currentDelay)
                currentDelay *= delayFactor
                attempt < 10
            }
    }

    suspend fun subscribeToBookingSession(): Flow<UserSession> {
        var currentDelay = 100L
        val delayFactor = 2
        return createCoroutineStub()
            .subscribeToSessionBooking(Empty.getDefaultInstance())
            .retryWhen { _, attempt ->
                Timber.e("Connection lost on subscribeToBookingSession attempt: $attempt")
                delay(currentDelay)
                currentDelay *= delayFactor
                attempt < 10
            }
    }

    private suspend fun createCoroutineStub(): SessionProtocolGrpcKt.SessionProtocolCoroutineStub {
        return SessionProtocolGrpcKt.SessionProtocolCoroutineStub(channel)
            .withCallCredentials(createCallCredentials(googleTokenRepository.getGoogleTokenId()))
    }

    private fun createCallCredentials(token: String): CallCredentials {
        return object: CallCredentials() {
            override fun applyRequestMetadata(
                requestInfo: RequestInfo?,
                appExecutor: Executor,
                applier: MetadataApplier
            ) {
                appExecutor.execute {
                    val header = Metadata()
                    header.put(key, "Bearer $token")
                    applier.apply(header)
                }
            }

            override fun thisUsesUnstableApi() { }
        }
    }
}