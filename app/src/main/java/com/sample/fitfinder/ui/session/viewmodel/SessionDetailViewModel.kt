package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.domain.Session
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
class SessionDetailViewModel @ViewModelInject constructor(private val sessionRepository: SessionRepository)
    : ViewModel() {

    var session: LiveData<Session>? = null

    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng>
        get() = _location

    fun setSessionId(id: Long) {
        session = sessionRepository.getSession(id)
        _location.value = session?.value?.locationCoordinate
    }
}