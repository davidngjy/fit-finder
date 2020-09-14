package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.SessionRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
class SessionAvailableViewModel @ViewModelInject constructor(sessionRepository: SessionRepository)
    : ViewModel() {

    val sessions = sessionRepository.getSessions()

    private val _navigateToSessionDetail = MutableLiveData<Long>()
    val navigateToSessionDetail: LiveData<Long>
        get() = _navigateToSessionDetail

    fun onSessionClick(sessionId: Long) {
        _navigateToSessionDetail.value = sessionId
    }

    fun onSessionDetailNavigated() {
        _navigateToSessionDetail.value = null
    }
}