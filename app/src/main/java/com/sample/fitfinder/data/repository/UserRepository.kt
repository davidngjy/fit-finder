package com.sample.fitfinder.data.repository

import com.sample.fitfinder.data.database.UserDao
import com.sample.fitfinder.data.database.UserEntity
import com.sample.fitfinder.data.gateway.UserGateway
import com.sample.fitfinder.domain.User
import com.sample.fitfinder.proto.LimitedUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor() {

    @Inject lateinit var userDao: UserDao
    @Inject lateinit var userGateway: UserGateway

    suspend fun getUserProfile(userId: Long): Flow<User> {
        return withContext(Dispatchers.IO){
            if (!userDao.userProfileExists(userId)){
                val userProfile = userGateway.getUserProfile(userId)
                insertUserProfile(userProfile)
            }
            userDao.getUser(userId)
        }
    }

    private suspend fun insertUserProfile(userProfile: LimitedUserProfile) {
        userDao.insertOrUpdate(UserEntity(
            userProfile.userId,
            userProfile.displayName,
            userProfile.email,
            userProfile.profilePicture.toByteArray()
        ))
    }
}