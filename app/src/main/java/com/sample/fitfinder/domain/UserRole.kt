package com.sample.fitfinder.domain

enum class UserRole(val value: Int) {
    ADMIN(1),
    TRAINER(2),
    USER(3);

    companion object {
        fun fromInt(value: Int) = values().first {it.value == value}
        fun toInt(value: UserRole) = value.value
    }
}