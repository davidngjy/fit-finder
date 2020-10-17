package com.sample.fitfinder.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.domain.BookingStatus
import java.time.Instant

@Entity(tableName = "Session")
data class SessionEntity (
    @PrimaryKey
    val sessionId: Long,
    val trainerUserId: Long,
    val title: String,
    val description: String,
    val sessionDateTime: Instant,
    val location: LatLng,
    val locationString: String,
    val isOnline: Boolean,
    val isInPerson: Boolean,
    val price: Double,
    val duration: Int,
    val bookingId: Long?,
    val clientUserId: Long?,
    val bookingStatus: BookingStatus
) {
//    companion object {
//        fun SessionEntity.toDomain() : Session{
//            return Session(
//                SessionId,
//                TrainerUserId,
//                Title,
//                Description,
//                SessionDateTime,
//                Location,
//                LocationString,
//                IsOnline,
//                IsInPerson,
//                Price,
//                Duration,
//                BookingId,
//                ClientUserId,
//                BookingStatus
//            )
//        }
//    }
}