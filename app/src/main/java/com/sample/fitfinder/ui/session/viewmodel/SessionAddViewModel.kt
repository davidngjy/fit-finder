package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import com.sample.fitfinder.data.repository.CurrentUserRepository
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.domain.Session
import com.sample.fitfinder.proto.Response
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@FragmentScoped
class SessionAddViewModel @ViewModelInject constructor(
    val sessionRepository: SessionRepository,
    val currentUserRepository: CurrentUserRepository)
    : ViewModel() {

    var sessionId: Long = 0

    // Two-way binding
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val dateString = MutableLiveData<String>()
    val timeString = MutableLiveData<String>()
    val locationString = MutableLiveData<String>()
    val price = MutableLiveData<Int>()
    val duration = MutableLiveData(30)
    val isOnline = MutableLiveData(true)
    val isInPerson = MutableLiveData(false)

    // DateTime
    val localDate = MutableLiveData<LocalDate>()
    val localTime = MutableLiveData<LocalTime>()

    // Location
    private val _locationCoordinate = MutableLiveData<LatLng>()
    val locationCoordinate: LiveData<LatLng>
        get() = _locationCoordinate

    private val _navigateOnConfirm = MutableLiveData<Boolean>()
    val navigateOnConfirm: LiveData<Boolean>
        get() = _navigateOnConfirm

    fun setCoordinate(coordinate: LatLng) {
        _locationCoordinate.value = coordinate
    }

    fun validateInput(layout: TextInputLayout): Boolean {
        if (layout.editText!!.text.isNullOrBlank()) {
            layout.isErrorEnabled = true
            layout.error = "Cannot be empty"
            return false
        }

        layout.error = null
        layout.isErrorEnabled = false
        return true
    }

    fun validateDateTime(): Boolean {
        if (localDate.value == null || localTime.value == null)
            return false

        return LocalDateTime.of(localDate.value, localTime.value)
            .atZone(ZoneId.systemDefault()).toInstant() >= Instant.now()
    }

    fun loadSessionDetail(sessionId: Long) {
        if (sessionId == this.sessionId) return

        viewModelScope.launch {
            sessionRepository.getSession(sessionId)
                .collect {sessionDetail ->
                    this@SessionAddViewModel.sessionId = sessionDetail.sessionId
                    title.value = sessionDetail.title
                    description.value = sessionDetail.description
                    locationString.value = sessionDetail.locationString
                    price.value = sessionDetail.price.toInt()
                    duration.value = sessionDetail.duration
                    isOnline.value = sessionDetail.isOnline
                    isInPerson.value = sessionDetail.isInPerson
                    _locationCoordinate.value = sessionDetail.location
                    setDateTimeFromSession(sessionDetail.sessionDateTime)
                }
        }
    }

    private fun setDateTimeFromSession(dateTimeInstant: Instant) {
        LocalDateTime.ofInstant(dateTimeInstant, ZoneId.systemDefault())
            .let {
                localDate.value = it.toLocalDate()
                localTime.value = it.toLocalTime()
                dateString.value = it.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM yyyy"))
                timeString.value = it.toLocalTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
            }
    }

    fun saveSession() {
        viewModelScope.launch {
            val session = Session(
                sessionId,
                currentUserRepository.userId.first(),
                title.value!!,
                description.value!!,
                LocalDateTime.of(localDate.value, localTime.value).atZone(ZoneId.systemDefault()).toInstant(),
                locationCoordinate.value!!,
                locationString.value!!,
                isOnline.value!!,
                isInPerson.value!!,
                price.value!!.toDouble(),
                duration.value!!
            )

            val response = sessionRepository.upsertSession(session)
            _navigateOnConfirm.value = response.resultStatus == Response.Status.Success
        }
    }
}