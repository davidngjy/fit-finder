package com.sample.fitfinder.ui.profile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.sample.fitfinder.data.repository.CurrentUserRepository
import com.sample.fitfinder.proto.UserProfile
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch

@FragmentScoped
class ProfileViewModel @ViewModelInject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val currentUserRepository: CurrentUserRepository) : ViewModel() {

    private val _currentUser = currentUserRepository.currentUser.asLiveData()
    val currentUser: LiveData<UserProfile>
        get() = _currentUser

    private val _logOutSuccessful = MutableLiveData<Boolean>()
    val logOutSuccessful: LiveData<Boolean>
        get() = _logOutSuccessful

    fun updateDisplayName(newName: String) {
        viewModelScope.launch {
            currentUserRepository.updateDisplayName(newName)
        }
    }

    fun listenToProfileChange() {
        viewModelScope.launch {
            currentUserRepository.subscribeToUserProfile()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            currentUserRepository.clearCurrentUser()
            googleSignInClient.signOut()
            _logOutSuccessful.value = true
        }
    }
}