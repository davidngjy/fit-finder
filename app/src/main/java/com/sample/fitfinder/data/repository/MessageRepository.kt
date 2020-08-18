package com.sample.fitfinder.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sample.fitfinder.domain.Message
import com.sample.fitfinder.domain.MessageSender
import com.sample.fitfinder.domain.MessageType
import java.time.LocalDateTime


class MessageRepository {
    fun getMessages() : LiveData<List<Message>> {
        val messages = arrayListOf<Message>(
            Message(1, MessageSender.ME, "Hello", MessageType.STRING, 1, LocalDateTime.now()),
            Message(2, MessageSender.OTHER, "Hey", MessageType.STRING, 2, LocalDateTime.now()),
            Message(3, MessageSender.OTHER, "How are you", MessageType.STRING, 2, LocalDateTime.now()),
            Message(4, MessageSender.ME, "I'm good thanks", MessageType.STRING, 1, LocalDateTime.now()),
            Message(5, MessageSender.ME, "You?", MessageType.STRING, 1, LocalDateTime.now())
            )
        val data = MutableLiveData<List<Message>>()
        data.value = messages
        return data
    }
}