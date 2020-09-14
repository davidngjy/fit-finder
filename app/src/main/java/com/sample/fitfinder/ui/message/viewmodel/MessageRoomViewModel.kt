package com.sample.fitfinder.ui.message.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.MessageRepository
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class MessageRoomViewModel @ViewModelInject constructor(messageRepository: MessageRepository)
    : ViewModel() {

    val messages = messageRepository.getMessages()
}