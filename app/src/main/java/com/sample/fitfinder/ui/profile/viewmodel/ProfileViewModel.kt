package com.sample.fitfinder.ui.profile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.sample.fitfinder.data.repository.CurrentUserRepository
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.launch

@FragmentScoped
class ProfileViewModel @ViewModelInject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val currentUserRepository: CurrentUserRepository) : ViewModel() {

    val currentUser = currentUserRepository.currentUser.asLiveData()

    private val _logOutSuccessful = MutableLiveData<Boolean>()
    val logOutSuccessful: LiveData<Boolean>
        get() = _logOutSuccessful

    fun logOut() {
        viewModelScope.launch {
            currentUserRepository.clearCurrentUser()
            googleSignInClient.signOut()
            _logOutSuccessful.value = true
        }
    }
}