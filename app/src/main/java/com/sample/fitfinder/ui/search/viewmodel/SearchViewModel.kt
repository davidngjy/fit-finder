package com.sample.fitfinder.ui.search.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.SessionRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
class SearchViewModel @ViewModelInject constructor(sessionRepository: SessionRepository)
    : ViewModel() {

    var sessions = sessionRepository.getSessions()
}