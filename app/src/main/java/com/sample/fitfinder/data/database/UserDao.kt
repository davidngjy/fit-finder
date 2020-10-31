package com.sample.fitfinder.data.database

import androidx.room.Dao
import androidx.room.Query
import com.sample.fitfinder.domain.User
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao : BaseDao<UserEntity>() {

    @Query("SELECT * FROM User WHERE userId = :userId")
    abstract fun getUser(userId: Long): Flow<User>

    @Query("SELECT EXISTS (SELECT 1 FROM User WHERE userId = :userId)")
    abstract suspend fun userProfileExists(userId: Long): Boolean
}