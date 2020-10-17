package com.sample.fitfinder.data.database

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.domain.BookingStatus
import com.sample.fitfinder.domain.UserRole
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.Instant

class DaoTypeConverter {
    // BookingStatus
    @TypeConverter
    fun toBookingStatusInt(value: BookingStatus) = BookingStatus.toInt(value)
    @TypeConverter
    fun toBookingStatus(value: Int) = BookingStatus.fromInt(value)

    // Instant
    @TypeConverter
    fun toInstantLong(value: Instant) = value.toEpochMilli()
    @TypeConverter
    fun toInstant(value: Long) = Instant.ofEpochMilli(value)

    // LatLng
    @TypeConverter
    fun toLatLngJson(value: LatLng) = Json.encodeToString(LatLngSerializer, value)
    @TypeConverter
    fun toLatLng(value: String) = Json.decodeFromString<LatLng>(LatLngSerializer, value)

    // UserRole
    @TypeConverter
    fun toInt(value: UserRole) = UserRole.toInt(value)
    @TypeConverter
    fun toUserRole(value: Int) = UserRole.fromInt(value)
}

object LatLngSerializer : KSerializer<LatLng> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LatLng", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LatLng {
        val customLatLngString = decoder.decodeString()
        val customLatLng = Json.decodeFromString<CustomLatLng>(customLatLngString)
        return LatLng(customLatLng.latitude, customLatLng.longitude)
    }

    override fun serialize(encoder: Encoder, value: LatLng) {
        val customLatLng = CustomLatLng(value.latitude, value.longitude)
        encoder.encodeString(Json.encodeToString(customLatLng))
    }

    @Serializable
    private data class CustomLatLng(val latitude: Double, val longitude : Double)
}