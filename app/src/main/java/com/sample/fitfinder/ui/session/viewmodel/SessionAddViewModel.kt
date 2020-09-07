package com.sample.fitfinder.ui.session.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputLayout
import java.time.Instant
import java.time.ZonedDateTime
import java.util.*

class SessionAddViewModel : ViewModel() {
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
    var locationCoordinate: LatLng = LatLng(0.0, 0.0)

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

    fun getConvertedDateTime(): Instant {
        val calendar = Calendar.getInstance()
        calendar.time = date.value!!.time
        calendar.set(Calendar.HOUR_OF_DAY, time.value!!.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, time.value!!.get(Calendar.MINUTE))
        return calendar.toInstant()
    }
}