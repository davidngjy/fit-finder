package com.sample.fitfinder.ui.message

import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.MessageRepository

class MessageRoomViewModel(messageRepository: MessageRepository)
    : ViewModel() {

    val messages = messageRepository.getMessages()
}