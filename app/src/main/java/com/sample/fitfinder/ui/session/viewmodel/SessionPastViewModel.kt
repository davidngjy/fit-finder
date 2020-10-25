package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sample.fitfinder.data.repository.SessionRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
class SessionPastViewModel @ViewModelInject constructor(
    sessionRepository: SessionRepository)
    : ViewModel() {

    val sessions = sessionRepository
        .getPastSessions()
        .asLiveData()
}