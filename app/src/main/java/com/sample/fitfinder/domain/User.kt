package com.sample.fitfinder.domain

data class User (
    val userId: Long,
    val displayName: String,
    val email: String,
    val profilePictureUri: String?
)