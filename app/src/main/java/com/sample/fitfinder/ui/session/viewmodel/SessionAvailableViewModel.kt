package com.sample.fitfinder.ui.session.viewmodel

import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.SessionRepository
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SessionAvailableViewModel(sessionRepository: SessionRepository) : ViewModel() {

    val sessions = sessionRepository.getSessions()
}