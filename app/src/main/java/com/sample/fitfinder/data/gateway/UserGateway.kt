package com.sample.fitfinder.data.gateway

import com.google.protobuf.ByteString
import com.google.protobuf.Empty
import com.sample.fitfinder.data.repository.GoogleTokenRepository
import com.sample.fitfinder.proto.*
import io.grpc.CallCredentials
import io.grpc.ManagedChannel
import io.grpc.Metadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.retryWhen
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject

class UserGateway @Inject constructor() {

    @Inject lateinit var channel: ManagedChannel
    @Inject lateinit var googleTokenRepository: GoogleTokenRepository

    private val key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

    suspend fun connectUser(token: String): Flow<ConnectUserResponse> {
        val stub = createCoroutineStub(false)

        val request = ConnectUserRequest
            .newBuilder()
            .setGoogleTokenId(token)
            .build()

        return stub.connectUser(request)
            .catch { ex ->
                Timber.e(ex, "Exception thrown while communicating to backend api.")
                emit(ConnectUserResponse.newBuilder().setStatus(ConnectUserResponse.Status.Failed).build())
            }
    }

    suspend fun updateUserProfile(displayName: String, email: String, profilePicture: ByteArray?)
        : Response{
        val stub = createCoroutineStub()

        val request = UpdateUserProfileRequest
            .newBuilder()
            .setDisplayName(displayName)
            .setEmail(email)
            .setProfilePicture(ByteString.copyFrom(profilePicture))
            .build()

        return stub.updateUserProfile(request)
    }

    suspend fun getUserProfile(userId: Long): LimitedUserProfile {
        return createCoroutineStub()
            .getUserProfile(UserProfileRequest
                .newBuilder()
                .setUserId(userId)
                .build()
            )
    }

    suspend fun subscribeToUserProfile(): Flow<UserProfile> {
        var currentDelay = 100L
        val delayFactor = 2
        return createCoroutineStub()
            .subscribeToUserProfile(Empty.getDefaultInstance())
            .retryWhen { _, attempt ->
                Timber.e("Connection lost on subscribeToUserProfile attempt: $attempt")
                delay(currentDelay)
                currentDelay *= delayFactor
                attempt < 10
            }
    }

    private suspend fun createCoroutineStub(withToken: Boolean = true): UserProtocolGrpcKt.UserProtocolCoroutineStub {
        return if (withToken) {
            UserProtocolGrpcKt.UserProtocolCoroutineStub(channel)
                .withCallCredentials(createCallCredentials(googleTokenRepository.getGoogleTokenId()))
        } else {
            UserProtocolGrpcKt.UserProtocolCoroutineStub(channel)
        }
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