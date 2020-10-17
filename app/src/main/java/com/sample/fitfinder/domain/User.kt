package com.sample.fitfinder.domain

data class User (
    val id: Long,
    val googleId: String,
    val displayName: String,
    val email: String,
    val role: UserRole,
    val profilePictureUri: String?
)