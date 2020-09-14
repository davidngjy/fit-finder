package com.sample.fitfinder.ui.profile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.sample.fitfinder.data.repository.UserRepository
import com.sample.fitfinder.domain.User
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
class ProfileViewModel @ViewModelInject constructor(userRepository: UserRepository)
    : ViewModel() {

    private val _currentUser = userRepository.getCurrentUser()
    val currentUser: LiveData<User>
        get() = _currentUser
}