package com.sample.fitfinder

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.fitfinder.data.repository.CurrentUserRepository
import com.sample.fitfinder.proto.ConnectUserResponse.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel @ViewModelInject constructor(
    private val currentUserRepository: CurrentUserRepository)
    : ViewModel() {

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun connectUser(token: String) {
        viewModelScope.launch {
            currentUserRepository.connectUser(token)
                .collect {
                    withContext(Dispatchers.Main) {
                        _status.value = it
                    }
                }
        }
    }

    fun setErrorMessage(msg: String) {
        _errorMessage.value = msg
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}