package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.sample.fitfinder.data.repository.CurrentUserRepository
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.proto.Response
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
@FlowPreview
@ExperimentalCoroutinesApi
class SessionDetailViewModel @ViewModelInject constructor(
    private val sessionRepository: SessionRepository,
    currentUserRepository: CurrentUserRepository)
    : ViewModel() {

    private val sessionIdChannel = BroadcastChannel<Long>(Channel.CONFLATED)
    private val sessionFlow = sessionIdChannel
        .asFlow()
        .flatMapLatest {
            sessionRepository.getSession(it)
        }

    val session = sessionFlow.asLiveData()

    val location = sessionFlow
        .map { it.location }
        .asLiveData()

    val isSessionOwner = currentUserRepository
        .userId
        .combine(sessionFlow) { userId, session ->
            userId == session.trainerUserId
        }
        .asLiveData()

    private val _navigateOnBooked = MutableLiveData<Boolean>()
    val navigateOnBooked: LiveData<Boolean>
        get() = _navigateOnBooked

    fun setSessionId(id: Long) {
        sessionIdChannel.offer(id)
    }

    fun addBooking(sessionId: Long) {
        viewModelScope.launch {
            val result = sessionRepository.addBooking(sessionId)
            if (result.resultStatus == Response.Status.Success)
                _navigateOnBooked.value = true
        }
    }
}