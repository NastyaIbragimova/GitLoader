package com.example.gitloader.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitloader.domain.DatabaseRepository
import com.example.gitloader.domain.models.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _savedRepositories = MutableStateFlow<List<Repository>>(emptyList())
    val savedRepositories: StateFlow<List<Repository>> = _savedRepositories.asStateFlow()

    init {
        loadSavedRepositories()
    }

    private fun loadSavedRepositories() {
        viewModelScope.launch {
            databaseRepository.getAllRepositories().collect { repositories ->
                _savedRepositories.value = repositories.map { repo ->
                    repo.copy(isDownloaded = true)
                }
            }
        }
    }
}