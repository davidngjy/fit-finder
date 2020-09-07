package com.sample.fitfinder.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.fitfinder.data.repository.SessionRepository
import kotlin.time.ExperimentalTime

@Suppress("UNCHECKED_CAST")
@ExperimentalTime
class SearchViewModelFactory (private val sessionRepository: SessionRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(sessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}