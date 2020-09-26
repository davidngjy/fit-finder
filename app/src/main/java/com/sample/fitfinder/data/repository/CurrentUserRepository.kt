package com.sample.fitfinder.data.repository

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.sample.fitfinder.data.UserProfileSerializer
import com.sample.fitfinder.data.gateway.UserGateway
import com.sample.fitfinder.proto.ConnectUserResponse.Status
import com.sample.fitfinder.proto.UserProfileOuterClass.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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

    suspend fun connectUser(token: String): Flow<Status> {
        return userGateway.connectUser(token)
            .onEach {
                if (it.status == Status.Connected) {
                    insertCurrentUser(it.userProfile)
                    googleTokenRepository.updateGoogleToken(token)
                }
            }
            .map { it.status }
    }

    private suspend fun insertCurrentUser(user: UserProfile) {
        userProfileDataStore.updateData { data ->
            data.toBuilder()
                .setId(user.id)
                .setGoogleId(user.googleId)
                .setDisplayName(user.displayName)
                .setEmail(user.email)
                .setUserRole(user.userRole)
                .setProfilePictureUri(user.profilePictureUri)
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