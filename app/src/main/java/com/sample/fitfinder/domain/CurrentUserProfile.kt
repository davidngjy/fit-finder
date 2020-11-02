package com.sample.fitfinder.domain

data class CurrentUserProfile (
    val userId: Long,
    val googleId: String,
    val displayName: String,
    val email: String,
    val profilePicture: ByteArray?,
    val userRole: UserRole
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrentUserProfile

        if (userId != other.userId) return false
        if (googleId != other.googleId) return false
        if (displayName != other.displayName) return false
        if (email != other.email) return false
        if (profilePicture != null) {
            if (other.profilePicture == null) return false
            if (!profilePicture.contentEquals(other.profilePicture)) return false
        } else if (other.profilePicture != null) return false
        if (userRole != other.userRole) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + googleId.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (profilePicture?.contentHashCode() ?: 0)
        result = 31 * result + userRole.hashCode()
        return result
    }

}