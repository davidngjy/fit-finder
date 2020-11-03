package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
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
        .getMyAvailableSessions()
        .asLiveData()
}