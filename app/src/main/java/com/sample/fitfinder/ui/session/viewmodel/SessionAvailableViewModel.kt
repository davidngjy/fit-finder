package com.sample.fitfinder.ui.session.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.SessionRepository
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SessionAvailableViewModel(sessionRepository: SessionRepository) : ViewModel() {

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