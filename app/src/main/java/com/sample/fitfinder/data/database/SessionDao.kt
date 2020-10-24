package com.sample.fitfinder.data.database

import androidx.room.Dao
import androidx.room.Query
import com.sample.fitfinder.domain.BookingStatus
import com.sample.fitfinder.domain.Session
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SessionDao: BaseDao<SessionEntity>() {
    @Query("""
        SELECT * 
        FROM Session
        WHERE BookingStatus = :type 
            AND TrainerUserId = :trainerUserId
        ORDER BY sessionDateTime""")
    abstract fun getSessions(type: BookingStatus, trainerUserId: Long): Flow<List<Session>>

    @Query("""
        SELECT *
        FROM Session
        WHERE BookingId = 0
    """)
    abstract fun getAvailableSessions(): Flow<List<Session>>

    @Query("""
        SELECT * 
        FROM Session
        WHERE SessionId = :sessionId """)
    abstract fun getSession(sessionId: Long): Flow<Session>

    @Query("SELECT EXISTS (SELECT 1 FROM Session WHERE sessionId = :sessionId)")
    abstract suspend fun sessionExists(sessionId: Long): Boolean

    @Query("DELETE FROM Session")
    abstract suspend fun clearAllSession()
}