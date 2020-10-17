package com.sample.fitfinder.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sample.fitfinder.domain.User
import com.sample.fitfinder.domain.UserRole

@Entity(tableName = "User")
data class UserEntity (
    @PrimaryKey
    val id: Long,
    val googleId: String,
    val displayName: String,
    val email: String,
    val role: UserRole,
    val profilePictureUri: String?
) {
    fun toDomainModel(): User = User(
        id,
        googleId,
        displayName,
        email,
        role,
        profilePictureUri
    )

    companion object {
        fun toEntityModel(user: User) = UserEntity(
            user.id,
            user.googleId,
            user.displayName,
            user.email,
            user.role,
            user.profilePictureUri
        )
    }
}