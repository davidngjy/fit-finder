package com.sample.fitfinder.data.repository

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.sample.fitfinder.data.UserProfileSerializer
import com.sample.fitfinder.data.gateway.UserGateway
import com.sample.fitfinder.domain.CurrentUserProfile
import com.sample.fitfinder.domain.UserRole
import com.sample.fitfinder.proto.ConnectUserResponse.Status
import com.sample.fitfinder.proto.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserRepository @Inject constructor(@ApplicationContext context: Context) {

    @Inject lateinit var userGateway: UserGateway
    @Inject lateinit var googleTokenRepository: GoogleTokenRepository

    private val userProfileDataStore: DataStore<UserProfile> =
        context.createDataStore(
            fileName = "user_profile.pb",
            serializer = UserProfileSerializer
        )

    val currentUser = userProfileDataStore.data
        .catch { ex ->
            if (ex is IOException) {
                Timber.e(ex, "Problem reading user profile data store")
                emit(UserProfile.getDefaultInstance())
            }
        }
        .map {
            CurrentUserProfile(
                it.id,
                it.googleId,
                it.displayName,
                it.email,
                it.profilePicture.toByteArray(),
                UserRole.fromInt(it.userRole.number)
            )
        }

    val userId = userProfileDataStore.data.map {
        it.id
    }

    suspend fun connectUser(token: String): Flow<Status> {
        return userGateway.connectUser(token)
            .onEach {
                if (it.status == Status.Connected) {
                    upsertCurrentUser(it.userProfile)
                    googleTokenRepository.updateGoogleToken(token)
                }
            }
            .map { it.status }
    }

    suspend fun updateDisplayName(newDisplayName: String) {
        val currentUser = currentUser.first()
        userGateway.updateUserProfile(
            newDisplayName,
            currentUser.email,
            currentUser.profilePicture
        )
    }

    suspend fun updateEmail(newEmail: String) {
        val currentUser = currentUser.first()
        userGateway.updateUserProfile(
            currentUser.displayName,
            newEmail,
            currentUser.profilePicture
        )
    }

    suspend fun updateProfilePicture(newProfilePicture: ByteArray) {
        val currentUser = currentUser.first()
        userGateway.updateUserProfile(
            currentUser.displayName,
            currentUser.email,
            newProfilePicture
        )
    }

    suspend fun upsertCurrentUser(user: UserProfile) {
        userProfileDataStore.updateData { data ->
            data.toBuilder()
                .setId(user.id)
                .setGoogleId(user.googleId)
                .setDisplayName(user.displayName)
                .setEmail(user.email)
                .setUserRole(user.userRole)
                .setProfilePicture(user.profilePicture)
                .build()
        }
    }

    suspend fun clearCurrentUser() {
        userProfileDataStore.updateData { data ->
            data.toBuilder().clear().build()
        }
        googleTokenRepository.clearToken()
    }
}