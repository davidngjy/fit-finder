package com.sample.fitfinder.ui.session.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.fitfinder.data.repository.SessionRepository

@Suppress("UNCHECKED_CAST")
class SessionAvailableViewModelFactory(private val sessionRepository: SessionRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionAvailableViewModel::class.java)) {
            return SessionAvailableViewModel(sessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}