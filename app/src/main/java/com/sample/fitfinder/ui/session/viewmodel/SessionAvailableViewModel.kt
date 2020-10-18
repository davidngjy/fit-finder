package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sample.fitfinder.data.repository.SessionRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
}