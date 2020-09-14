package com.sample.fitfinder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(RoleConverter::class)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}