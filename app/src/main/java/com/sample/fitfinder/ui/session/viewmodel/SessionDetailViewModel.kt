package com.sample.fitfinder.ui.session.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.domain.Session
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SessionDetailViewModel() : ViewModel() {
    private val sessionRepository = SessionRepository

    var session: LiveData<Session>? = null

    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng>
        get() = _location

    fun setSessionId(id: Long) {
        session = sessionRepository.getSession(id)
        _location.value = session?.value?.locationCoordinate
    }
}