package com.sample.fitfinder.data.gateway

import com.sample.fitfinder.data.repository.GoogleTokenRepository
import com.sample.fitfinder.proto.ConnectUserRequest
import com.sample.fitfinder.proto.ConnectUserResponse
import com.sample.fitfinder.proto.UserProtocolGrpc
import com.sample.fitfinder.proto.UserProtocolGrpc.UserProtocolBlockingStub
import com.sample.fitfinder.proto.UserProtocolGrpc.UserProtocolFutureStub
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import javax.inject.Inject

class UserGateway @Inject constructor() {

    @Inject lateinit var channel: ManagedChannel
    @Inject lateinit var googleTokenRepository: GoogleTokenRepository

    private val key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)

    suspend fun connectUser(token: String): Flow<ConnectUserResponse> {
        val stub = createBlockingStub(false)

        val request = ConnectUserRequest
            .newBuilder()
            .setGoogleTokenId(token)
            .build()

        return stub.connectUser(request).asFlow()
            .catch { ex ->
                Timber.e(ex, "Exception thrown while communicating to backend api.")
                emit(ConnectUserResponse.newBuilder().setStatus(ConnectUserResponse.Status.Failed).build())
            }
    }

    private suspend fun createBlockingStub(withToken: Boolean): UserProtocolBlockingStub {
        return if (withToken) {
            val header = Metadata()
            header.put(key, "Bearer ${googleTokenRepository.getGoogleTokenId()}")
            val stub = UserProtocolGrpc.newBlockingStub(channel)
            MetadataUtils.attachHeaders(stub, header)
        } else {
            UserProtocolGrpc.newBlockingStub(channel)
        }
    }

    private suspend fun createFutureStub(withToken: Boolean): UserProtocolFutureStub {
        return if (withToken) {
            val header = Metadata()
            header.put(key, "Bearer ${googleTokenRepository.getGoogleTokenId()}")
            val stub = UserProtocolGrpc.newFutureStub(channel)
            MetadataUtils.attachHeaders(stub, header)
        } else {
            UserProtocolGrpc.newFutureStub(channel)
        }
    }
}