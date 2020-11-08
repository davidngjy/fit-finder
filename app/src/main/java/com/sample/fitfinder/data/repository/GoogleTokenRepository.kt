package com.sample.fitfinder.data.repository

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.sample.fitfinder.data.GoogleTokenSerializer
import com.sample.fitfinder.proto.GoogleToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleTokenRepository @Inject constructor(@ApplicationContext context: Context) {

    @Inject lateinit var googleSignInClient: GoogleSignInClient

    private val googleTokenDataStore: DataStore<GoogleToken> =
        context.createDataStore(
            fileName = "google_token.pb",
            serializer = GoogleTokenSerializer
        )

    private val googleToken = googleTokenDataStore.data
        .catch { ex ->
            if (ex is IOException) {
                Timber.e(ex, "Problem reading google token data store.")
                emit(GoogleToken.getDefaultInstance())
            }
        }

    suspend fun updateGoogleToken(tokenId: String?) {
        googleTokenDataStore.updateData { data ->
            data.toBuilder()
                .setTokenId(tokenId)
                .setExpiredDateTime(Instant.now().plus(50, ChronoUnit.MINUTES).toEpochMilli()) // 50 minutes to expiry time
                .build()
        }
    }

    suspend fun getGoogleTokenId(): String {
        return withContext(Dispatchers.IO) {
            if (isTokenExpired()) {
                refreshGoogleToken()
            }
            googleToken.first().tokenId
        }
    }

    private suspend fun isTokenExpired(): Boolean {
        return Instant.now().toEpochMilli() > googleToken.first().expiredDateTime
    }

    private suspend fun refreshGoogleToken() {
        withContext(Dispatchers.IO) {
            googleSignInClient.silentSignIn().addOnCompleteListener {
                launch(Dispatchers.Default) {
                    try {
                        val result = it.getResult(ApiException::class.java)
                        updateGoogleToken(result!!.idToken!!)
                    } catch (ex: ApiException) {
                        Timber.e(ex, "Unable to refresh token")
                        updateGoogleToken(null)
                    }
                }
            }
        }
    }

    suspend fun clearToken() {
        withContext(Dispatchers.IO) {
            googleTokenDataStore.updateData { data ->
                data.toBuilder().clear().build()
            }
        }
    }
}