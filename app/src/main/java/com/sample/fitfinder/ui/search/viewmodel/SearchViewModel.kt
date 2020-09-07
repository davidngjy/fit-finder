package com.sample.fitfinder.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.SessionRepository
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SearchViewModel(sessionRepository: SessionRepository) : ViewModel() {

    var sessions = sessionRepository.getSessions()
}