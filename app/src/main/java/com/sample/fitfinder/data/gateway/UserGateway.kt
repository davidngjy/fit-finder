package com.sample.fitfinder.data.gateway

import android.annotation.SuppressLint
import com.sample.fitfinder.data.repository.GoogleTokenRepository
import com.sample.fitfinder.proto.*
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
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

    @SuppressLint("CheckResult")
    suspend fun updateUserProfile(userId: Long, displayName: String, email: String, profilePictureUrl: String) {
        val stub = createCoroutineStub()

        val request = UpdateUserProfileRequest
            .newBuilder()
            .setUserId(userId)
            .setDisplayName(displayName)
            .setEmail(email)
            .setProfilePictureUri(profilePictureUrl)
            .build()

        stub.updateUserProfile(request)
    }

    suspend fun subscribeToUserProfile(userId: Long): Flow<UserProfile> {
        val stub = createCoroutineStub()

        val request = SubscribeUserProfileRequest
            .newBuilder()
            .setId(userId)
            .build()

        return stub.subscribeToUserProfile(request)
            .catch { ex ->
                Timber.e(ex, "Exception thrown on subscribe to user profile.")
            }
    }

    private suspend fun createCoroutineStub(withToken: Boolean = true): UserProtocolGrpcKt.UserProtocolCoroutineStub {
        return if (withToken) {
            val header = Metadata()
            header.put(key, "Bearer ${googleTokenRepository.getGoogleTokenId()}")
            val stub = UserProtocolGrpcKt.UserProtocolCoroutineStub(channel)
            MetadataUtils.attachHeaders(stub, header)
        } else {
            UserProtocolGrpcKt.UserProtocolCoroutineStub(channel)
        }
    }
}