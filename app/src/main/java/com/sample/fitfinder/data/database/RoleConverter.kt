package com.sample.fitfinder.data.database

import androidx.room.TypeConverter
import com.sample.fitfinder.domain.UserRole

class RoleConverter {
    @TypeConverter
    fun fromUserRole(value: UserRole) = UserRole.toInt(value)

    @TypeConverter
    fun toUserRole(value: Int) = UserRole.fromInt(value)
}