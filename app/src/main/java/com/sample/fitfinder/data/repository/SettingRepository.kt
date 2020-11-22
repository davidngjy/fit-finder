package com.sample.fitfinder.data.repository

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.google.protobuf.Duration
import com.google.protobuf.Timestamp
import com.sample.fitfinder.data.SearchFilterSerializer
import com.sample.fitfinder.domain.SessionFilter
import com.sample.fitfinder.proto.SearchFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(@ApplicationContext context: Context) {

    private val searchFilterDataStore: DataStore<SearchFilter> =
        context.createDataStore(
            fileName = "search_filter.pb",
            serializer = SearchFilterSerializer
        )

    val sessionFilter = searchFilterDataStore.data
        .map {
            SessionFilter(
                it.maxPrice,
                it.online,
                it.inPerson,
                (it.upperDuration.seconds/MIN_IN_SECOND).toInt(),
                (it.lowerDuration.seconds/MIN_IN_SECOND).toInt(),
                Instant.ofEpochSecond(it.upperDateTime.seconds),
                Instant.ofEpochSecond(it.lowerDateTime.seconds)
            )
        }

    suspend fun resetDefaultSearchFilter() {
        val dateTimeNow = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val dateTimeAMonthAhead = dateTimeNow.plusMonths(1).minusMinutes(1)

        searchFilterDataStore.updateData {
            it.toBuilder()
                .setMaxPrice(200)
                .setOnline(true)
                .setInPerson(true)
                .setUpperDuration(Duration.newBuilder().setSeconds((MIN_IN_SECOND * 120).toLong()).build())
                .setLowerDuration(Duration.newBuilder().setSeconds((MIN_IN_SECOND * 30).toLong()).build())
                .setUpperDateTime(Timestamp.newBuilder().setSeconds(dateTimeAMonthAhead.toEpochSecond()).build())
                .setLowerDateTime(Timestamp.newBuilder().setSeconds(dateTimeNow.toEpochSecond()).build())
                .build()
        }
    }

    suspend fun setMaxPrice(maxPrice: Int) {
        searchFilterDataStore.updateData {
            it.toBuilder()
                .setMaxPrice(maxPrice)
                .build()
        }
    }

    suspend fun setSessionType(isOnline: Boolean, isInPerson: Boolean) {
        searchFilterDataStore.updateData {
            it.toBuilder()
                .setOnline(isOnline)
                .setInPerson(isInPerson)
                .build()
        }
    }

    suspend fun setDuration(upperDuration: Int, lowerDuration: Int) {
        searchFilterDataStore.updateData {
            it.toBuilder()
                .setUpperDuration(Duration.newBuilder().setSeconds((upperDuration*60).toLong()).build())
                .setLowerDuration(Duration.newBuilder().setSeconds((lowerDuration*60).toLong()).build())
                .build()
        }
    }

    suspend fun setDateTime(lowerDateTimeEpochSecond: Long, upperDateTimeEpochSecond: Long) {
        searchFilterDataStore.updateData {
            it.toBuilder()
                .setLowerDateTime(Timestamp.newBuilder().setSeconds(lowerDateTimeEpochSecond).build())
                .setUpperDateTime(Timestamp.newBuilder().setSeconds(upperDateTimeEpochSecond).build())
                .build()
        }
    }

    companion object {
        private const val MIN_IN_SECOND = 60
    }
}