package com.sample.fitfinder.ui.message.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.MessageRepository
import com.sample.fitfinder.domain.MessageUserList
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class MessageViewModel @ViewModelInject constructor(messageRepository: MessageRepository)
    : ViewModel() {

    val rooms = messageRepository.getMessageRooms()

    private val _navigateToMessageRoom = MutableLiveData<MessageUserList>()
    val navigateToMessageRoom: LiveData<MessageUserList>
        get() = _navigateToMessageRoom

    fun onMessageRoomClick(message: MessageUserList) {
        _navigateToMessageRoom.value = message
    }

    fun onMessageRoomNavigated() {
        _navigateToMessageRoom.value = null
    }
}