package com.sample.fitfinder

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sample.fitfinder.data.repository.CurrentUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapLatest

@FlowPreview
@ExperimentalCoroutinesApi
class LoginViewModel @ViewModelInject constructor(
    private val currentUserRepository: CurrentUserRepository)
    : ViewModel() {

    private val tokenChannel = BroadcastChannel<String>(Channel.CONFLATED)
    val status = tokenChannel
        .asFlow()
        .flatMapLatest {
            currentUserRepository.connectUser(it)
        }
        .asLiveData()

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun connectUser(token: String) {
        tokenChannel.offer(token)
    }

    fun setErrorMessage(msg: String) {
        _errorMessage.value = msg
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}