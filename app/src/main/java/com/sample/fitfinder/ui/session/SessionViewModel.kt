package com.sample.fitfinder.ui.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SessionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is session Fragment"
    }
    val text: LiveData<String> = _text
}