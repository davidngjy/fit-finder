package com.sample.fitfinder.ui.profile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.fitfinder.data.repository.CurrentUserRepository
import com.sample.fitfinder.ui.profile.EditAction
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@FragmentScoped
class ProfileEditViewModel @ViewModelInject constructor(
    private val currentUserRepository: CurrentUserRepository
) : ViewModel() {

    private val _doneUpdate = MutableLiveData<Boolean>()
    val doneUpdate: LiveData<Boolean>
        get() = _doneUpdate

    private val _title = MutableLiveData<String>()
    val title: LiveData<String>
        get() = _title

    val inputValue = MutableLiveData<String>()

    private val _action = MutableLiveData<EditAction>()
    val action: LiveData<EditAction>
        get() = _action

    fun setEditAction(action: EditAction) {
        when (action) {
            EditAction.DisplayName -> setupEditDisplayName()
            EditAction.Email -> setupEditEmail()
        }
    }

    private fun setupEditDisplayName() {
        _action.value = EditAction.DisplayName
        viewModelScope.launch {
            val userProfile = currentUserRepository.currentUser.first()
            _title.value = "Enter your display name"
            inputValue.value = userProfile.displayName
        }
    }

    private fun setupEditEmail() {
        _action.value = EditAction.Email
        viewModelScope.launch {
            val userProfile = currentUserRepository.currentUser.first()
            _title.value = "Enter your email"
            inputValue.value = userProfile.email
        }
    }

    fun confirmEdit() {
        viewModelScope.launch {
            when (_action.value) {
                EditAction.DisplayName -> currentUserRepository.updateDisplayName(inputValue.value!!)
                EditAction.Email -> currentUserRepository.updateEmail(inputValue.value!!)
            }
            _doneUpdate.value = true
        }
    }
}