package com.sample.fitfinder

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.fitfinder.data.repository.SessionRepository
import com.sample.fitfinder.data.repository.SettingRepository
import com.sample.fitfinder.data.repository.SubscriptionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MainViewModel @ViewModelInject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val sessionRepository: SessionRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {
    fun subscribe() {
        viewModelScope.launch {
            awaitAll(
                async { subscriptionRepository.subscribeToUserSession() },
                async { subscriptionRepository.subscribeToProfileUpdate() },
                async { subscriptionRepository.subscribeToBookingSession() }
            )
        }
    }

    fun clear() {
        viewModelScope.launch {
            sessionRepository.clearAllSession()
            settingRepository.resetDefaultSearchFilter()
        }
    }
}