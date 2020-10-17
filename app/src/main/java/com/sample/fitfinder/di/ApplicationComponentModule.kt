package com.sample.fitfinder.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.sample.fitfinder.BuildConfig
import com.sample.fitfinder.R
import com.sample.fitfinder.data.database.ApplicationDatabase
import com.sample.fitfinder.data.database.SessionDao
import com.sample.fitfinder.data.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object ApplicationComponentModule {
    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext appContext: Context)
            : GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(appContext.getString(R.string.server_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(appContext, gso)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): ApplicationDatabase {
        return Room.databaseBuilder(
            appContext,
            ApplicationDatabase::class.java,
            "fitfinder.db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: ApplicationDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideSessionDao(database: ApplicationDatabase): SessionDao {
        return database.sessionDao()
    }

    @Provides
    @Singleton
    fun provideManagedChannel(@ApplicationContext appContext: Context): ManagedChannel {
        val serviceUrl = appContext.getString(R.string.service_url)
        return if (BuildConfig.LOCAL_BACKEND) {
            ManagedChannelBuilder.forAddress(serviceUrl, 5001)
                .usePlaintext()
                .build()
        } else {
            ManagedChannelBuilder.forTarget(serviceUrl)
                .useTransportSecurity()
                .build()
        }
    }
}