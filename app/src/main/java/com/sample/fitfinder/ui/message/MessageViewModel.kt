package com.sample.fitfinder.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.MessageRepository

class MessageViewModel(private val messageRepository: MessageRepository)
    : ViewModel() {

    val rooms = messageRepository.getMessageRooms()

    private val _navigateToMessageRoom = MutableLiveData<Long>()
    val navigateToMessageRoom
        get() = _navigateToMessageRoom

    fun onMessageRoomClick(id: Long) {
        _navigateToMessageRoom.value = id
    }

    fun onMessageRoomNavigated() {
        _navigateToMessageRoom.value = null
    }
}