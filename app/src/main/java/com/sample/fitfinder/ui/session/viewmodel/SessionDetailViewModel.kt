package com.sample.fitfinder.ui.session.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sample.fitfinder.data.repository.SessionRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

@FragmentScoped
@ExperimentalTime
@FlowPreview
@ExperimentalCoroutinesApi
class SessionDetailViewModel @ViewModelInject constructor(private val sessionRepository: SessionRepository)
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

    fun setSessionId(id: Long) {
        sessionIdChannel.offer(id)
    }
}