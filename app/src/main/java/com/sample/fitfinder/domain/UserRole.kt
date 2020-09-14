package com.sample.fitfinder.domain

enum class UserRole(val value: Int) {
    ADMIN(0),
    TRAINER(1),
    USER(2);

    companion object {
        fun fromInt(value: Int) = values().first {it.value == value}
        fun toInt(value: UserRole) = value.value
    }
}