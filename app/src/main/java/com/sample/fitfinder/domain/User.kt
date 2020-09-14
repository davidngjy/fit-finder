package com.sample.fitfinder.domain

data class User (
    val Id: Long,
    val googleId: String,
    val displayName: String,
    val email: String,
    val role: UserRole,
    val profilePictureUri: String?
)