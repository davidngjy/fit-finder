package com.sample.fitfinder.domain

data class User (
    val userId: Long,
    val displayName: String,
    val userName: String,
    val email: String,
    val pathToProfilePicture: String?
)