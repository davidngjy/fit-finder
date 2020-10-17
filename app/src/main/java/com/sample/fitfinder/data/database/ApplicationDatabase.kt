package com.sample.fitfinder.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserEntity::class, SessionEntity::class], version = 1, exportSchema = false)
@TypeConverters(DaoTypeConverter::class)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
}