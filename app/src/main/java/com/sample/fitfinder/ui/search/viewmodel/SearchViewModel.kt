package com.sample.fitfinder.ui.search.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.CurrentUserRepository
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.domain.Session
import dagger.hilt.android.scopes.FragmentScoped
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
class SearchViewModel @ViewModelInject constructor(
    sessionRepository: SessionRepository,
    currentUserRepository: CurrentUserRepository)
    : ViewModel() {

    private val _nonBookSessions = MutableLiveData<List<Session>>()
    val nonBookSessions: LiveData<List<Session>>
        get() = _nonBookSessions

    init {
//        viewModelScope.launch {
//            val userId = currentUserRepository.currentUser.first().id
//            sessionRepository.getNonBookSessions(userId)
//                .collect {
//                    _nonBookSessions.value = it
//                }
//        }
    }
}