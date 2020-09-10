package com.sample.fitfinder.ui.session.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.domain.Session
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*
import kotlin.time.ExperimentalTime

class SessionAddViewModel : ViewModel() {
    @ExperimentalTime
    private val sessionRepository = SessionRepository
    var sessionId: Long = 0

    private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

    // Two-way binding
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val dateString = MutableLiveData<String>()
    val timeString = MutableLiveData<String>()
    val locationString = MutableLiveData<String>()
    val cost = MutableLiveData<Int>()
    val duration = MutableLiveData(30)
    val isOnline = MutableLiveData(true)
    val isInPerson = MutableLiveData(false)

    // DateTime
    val date = MutableLiveData(Calendar.getInstance())
    val time = MutableLiveData(Calendar.getInstance())

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

    fun validateDateTime(timeLayout: TextInputLayout): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date.value!!.time
        calendar.set(Calendar.HOUR_OF_DAY, time.value!!.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, time.value!!.get(Calendar.MINUTE))
        if (calendar.toInstant() < ZonedDateTime.now().toInstant()) {
            timeLayout.isErrorEnabled = true
            timeLayout.error = "Time cannot be in the past"
            return false
        }

        timeLayout.error = null
        timeLayout.isErrorEnabled = false
        return true
    }

    private fun getConvertedDateTime(): Instant {
        val calendar = Calendar.getInstance()
        calendar.time = date.value!!.time
        calendar.set(Calendar.HOUR_OF_DAY, time.value!!.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, time.value!!.get(Calendar.MINUTE))
        return calendar.toInstant()
    }

    @ExperimentalTime
    fun loadSessionDetail(sessionId: Long) {
        if (sessionId == this.sessionId) return

        val sessionDetail = sessionRepository.getSession(sessionId).value!!
        this.sessionId = sessionDetail.id
        title.value = sessionDetail.title
        description.value = sessionDetail.description
        locationString.value = sessionDetail.locationSuburbString
        cost.value = sessionDetail.cost
        duration.value = sessionDetail.durationInMin
        isOnline.value = sessionDetail.isOnline
        isInPerson.value = sessionDetail.isInPerson
        _locationCoordinate.value = sessionDetail.locationCoordinate
        setDateTimeFromSession(sessionDetail.sessionDateTime)
    }

    private fun setDateTimeFromSession(dateTimeInstant: Instant) {
        val calendar = Calendar.getInstance()
        calendar.time = Date(dateTimeInstant.toEpochMilli())
        date.value = calendar
        time.value = calendar

        timeString.value = timeFormatter.format(calendar.time)
        dateString.value = dateFormatter.format(calendar.time)
    }

    @ExperimentalTime
    fun saveSession() {
        val newSession = Session(
            1,
            1,
            title.value!!,
            description.value!!,
            getConvertedDateTime(),
            locationCoordinate.value!!,
            locationString.value!!,
            isOnline.value!!,
            isInPerson.value!!,
            cost.value!!,
            duration.value!!
        )
        sessionRepository.addSession(newSession)

        _navigateOnConfirm.value = true
    }
}