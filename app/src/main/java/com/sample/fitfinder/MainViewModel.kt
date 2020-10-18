package com.sample.fitfinder

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.fitfinder.data.repository.SubscriptionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    fun subscribe() {
        viewModelScope.launch {
            awaitAll(
                async { subscriptionRepository.subscribeToUserSession() },
                async { subscriptionRepository.subscribeToAvailableSession() },
                async { subscriptionRepository.subscribeToProfileUpdate() }
            )
        }
    }
}