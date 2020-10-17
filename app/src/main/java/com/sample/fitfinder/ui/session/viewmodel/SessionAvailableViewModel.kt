package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sample.fitfinder.data.repository.SessionRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
class SessionAvailableViewModel @ViewModelInject constructor(
    val sessionRepository: SessionRepository)
    : ViewModel() {

    @ExperimentalCoroutinesApi
    val sessions = sessionRepository
        .getNonBookSessions()
        .asLiveData()

    private val _navigateToSessionDetail = MutableLiveData<Long>()
    val navigateToSessionDetail: LiveData<Long>
        get() = _navigateToSessionDetail

    fun onSessionClick(sessionId: Long) {
        _navigateToSessionDetail.value = sessionId
    }

    fun onSessionDetailNavigated() {
        _navigateToSessionDetail.value = null
    }

    fun subscribeToUserSession() {
        viewModelScope.launch {
            sessionRepository.subscribeToUserSession()
        }
    }
}