package com.sample.fitfinder.ui.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.fitfinder.data.repository.MessageRepository

@Suppress("UNCHECKED_CAST")
class MessageRoomViewModelFactory(private val messageRepository: MessageRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageRoomViewModel::class.java)) {
            return MessageRoomViewModel(messageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}