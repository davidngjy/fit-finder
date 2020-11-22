package com.sample.fitfinder.ui.search.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sample.fitfinder.data.repository.SettingRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

class SearchFilterViewModel @ViewModelInject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    // two-way binding
    val maxPrice = MutableLiveData<Int>()
    val isOnline = MutableLiveData<Boolean>()
    val isInPerson = MutableLiveData<Boolean>()
    val upperDuration = MutableLiveData<Int>()
    val lowerDuration = MutableLiveData<Int>()

    private val upperLocalDate = MutableLiveData<LocalDate>()
    private val lowerLocalDate = MutableLiveData<LocalDate>()
    private val upperLocalTime = MutableLiveData<LocalTime>()
    private val lowerLocalTime = MutableLiveData<LocalTime>()

    @FlowPreview
    @ExperimentalTime
    val durationString = lowerDuration
        .asFlow()
        .combine(upperDuration.asFlow()) { lower, upper ->
            "$lower min - $upper min"
        }
        .debounce(10.milliseconds)
        .asLiveData()

    @FlowPreview
    @ExperimentalTime
    val dateRangeString = lowerLocalDate
        .asFlow()
        .debounce(50.milliseconds)
        .combine(upperLocalDate.asFlow()) { lower, upper ->
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            "${lower.format(formatter)} - ${upper.format(formatter)}"
        }
        .debounce(50.milliseconds)
        .asLiveData()

    val lowerTimeString = lowerLocalTime
        .asFlow()
        .map {
            it.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        }
        .asLiveData()

    val upperTimeString = upperLocalTime
        .asFlow()
        .map {
            it.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        }
        .asLiveData()

    init {
        viewModelScope.launch {
            settingRepository.sessionFilter.collect {
                maxPrice.value = it.maxPrice
                isOnline.value = it.online
                isInPerson.value = it.inPerson
                upperDuration.value = it.upperDuration
                lowerDuration.value = it.lowerDuration

                // Device's time zone
                val zonedUpperDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.upperDateTime.epochSecond), ZoneId.systemDefault())
                val zonedLowerDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(it.lowerDateTime.epochSecond), ZoneId.systemDefault())

                upperLocalDate.value = zonedUpperDateTime.toLocalDate()
                lowerLocalDate.value = zonedLowerDateTime.toLocalDate()
                upperLocalTime.value = zonedUpperDateTime.toLocalTime()
                lowerLocalTime.value = zonedLowerDateTime.toLocalTime()
            }
        }
    }

    fun updateMaxPrice() {
        viewModelScope.launch {
            settingRepository.setMaxPrice(maxPrice.value!!)
        }
    }

    fun updateSessionType() {
        viewModelScope.launch {
            settingRepository.setSessionType(
                isOnline.value!!,
                isInPerson.value!!
            )
        }
    }

    fun updateDuration() {
        viewModelScope.launch {
            settingRepository.setDuration(
                upperDuration.value!!,
                lowerDuration.value!!
            )
        }
    }

    fun updateDate(lowerDateInMillis: Long, upperDateInMillis: Long) {
        lowerLocalDate.value = LocalDateTime
            .ofInstant(Instant.ofEpochMilli(lowerDateInMillis), ZoneId.systemDefault())
            .toLocalDate()
        upperLocalDate.value = LocalDateTime
            .ofInstant(Instant.ofEpochMilli(upperDateInMillis), ZoneId.systemDefault())
            .toLocalDate()

        updateDateTime()
    }

    fun updateLowerTime(localTime: LocalTime) {
        lowerLocalTime.value = localTime
        updateDateTime()
    }

    fun updateUpperTime(localTime: LocalTime) {
        upperLocalTime.value = localTime
        updateDateTime()
    }

    private fun updateDateTime() {
        val lowerLocalDateTime = LocalDateTime.of(lowerLocalDate.value, lowerLocalTime.value)
        val upperLocalDateTime = LocalDateTime.of(upperLocalDate.value, upperLocalTime.value)

        viewModelScope.launch {
            settingRepository
                .setDateTime(
                    lowerLocalDateTime.atZone(ZoneId.systemDefault()).toEpochSecond(),
                    upperLocalDateTime.atZone(ZoneId.systemDefault()).toEpochSecond())
        }
    }
}