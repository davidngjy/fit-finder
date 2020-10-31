package com.sample.fitfinder.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class UserEntity (
    @PrimaryKey
    val userId: Long,
    val displayName: String,
    val email: String,
    val profilePictureUri: String?
)