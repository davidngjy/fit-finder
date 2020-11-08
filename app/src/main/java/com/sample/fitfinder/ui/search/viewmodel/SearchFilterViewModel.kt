package com.sample.fitfinder.ui.search.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sample.fitfinder.data.repository.SettingRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
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
    val upperDateTime = MutableLiveData<LocalDateTime>()
    val lowerDateTime = MutableLiveData<LocalDateTime>()

    @FlowPreview
    @ExperimentalTime
    val durationString = lowerDuration
        .asFlow()
        .combine(upperDuration.asFlow()) { lower, upper ->
            "$lower min - $upper min"
        }
        .debounce(10.milliseconds)
        .asLiveData()

    val dateRangeString = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            settingRepository.sessionFilter.collect {
                maxPrice.value = it.maxPrice
                isOnline.value = it.online
                isInPerson.value = it.inPerson
                upperDuration.value = it.upperDuration
                lowerDuration.value = it.lowerDuration

                upperDateTime.value = LocalDateTime.ofEpochSecond(it.upperDateTime.epochSecond, it.upperDateTime.nano, ZoneOffset.UTC)

//                val upperDateCalendar = Calendar.getInstance()
//                upperDateCalendar.time = Date(it.upperDateTime.toEpochMilli())
//                upperDateTime.value = upperDateCalendar
//
//                val lowerDateCalendar = Calendar.getInstance()
//                lowerDateCalendar.time = Date(it.lowerDateTime.toEpochMilli())
//                lowerDateTime.value = lowerDateCalendar
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

//    fun updateDateTime() {
//        viewModelScope.launch {
//            settingRepository.setDateTime(
//                upperDateTime.value!!.toInstant(),
//                lowerDateTime.value!!.toInstant()
//            )
//        }
//    }
}