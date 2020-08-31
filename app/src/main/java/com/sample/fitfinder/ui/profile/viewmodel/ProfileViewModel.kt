package com.sample.fitfinder.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.UserRepository
import com.sample.fitfinder.domain.User

class ProfileViewModel(userRepository: UserRepository) : ViewModel() {
    private val _currentUser = userRepository.getCurrentUser()
    val currentUser: LiveData<User>
        get() = _currentUser
}