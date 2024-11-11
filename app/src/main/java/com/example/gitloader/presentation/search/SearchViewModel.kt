package com.example.gitloader.presentation.search

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gitloader.data.utils.FileDownloader
import com.example.gitloader.domain.DatabaseRepository
import com.example.gitloader.domain.NetworkRepository
import com.example.gitloader.domain.models.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository,
    private val fileDownloader: FileDownloader
) : ViewModel() {

    val isError = mutableStateOf(false)
    var errorMessage = ""
        private set

    var isDownloadError = mutableStateOf(false)
        private set

    var repoResponse: MutableState<List<Repository>> = mutableStateOf(listOf())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var searchQuery = mutableStateOf("")
        private set

    var isSearchPerformed = mutableStateOf(false)
        private set

    var isFileDownloaded = mutableStateOf(false)
        private set

    var progressMap = mutableStateOf<Map<String, String>>(emptyMap())
        private set

    private var downloadedRepos: List<Repository> = emptyList()

    init {
        viewModelScope.launch {
            databaseRepository.getAllRepositories().collect { repos ->
                downloadedRepos = repos
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun getRepos(username: String) {
        isLoading.value = true
        isError.value = false
        isSearchPerformed.value = false
        viewModelScope.launch {
            networkRepository.getRepos(username).onSuccess { repositoryList ->
                if (repositoryList != null) {
                    val updatedRepos = repositoryList.map { repo ->
                        repo.copy(isDownloaded = downloadedRepos.any { it.id == repo.id })
                    }
                    repoResponse.value = updatedRepos
                }
            }.onFailure {
                isError.value = true
                errorMessage = it.message.toString()
            }
            isLoading.value = false
            isSearchPerformed.value = true
        }
    }

    fun downloadZip(repo: Repository) {
        updateProgress(repo.name, FileDownloader.FILE_DOWNLOADING)
        val ref = repo.defaultBranch
        viewModelScope.launch {
            networkRepository.downloadZip(repo.owner, repo.name, ref).onSuccess {
                it?.let { responseBody ->
                    val filename = "${repo.name}-$ref.zip"
                    fileDownloader.saveFileToDownloads(
                        responseBody,
                        filename,
                        onComplete = {
                            updateProgress(repo.name, FileDownloader.FILE_DOWNLOADED)
                            isFileDownloaded.value = true
                            saveRepository(repo)
                        },
                        onError = { error ->
                            updateProgress(repo.name, "")
                            isDownloadError.value = true
                            errorMessage = error.message.toString()
                        })
                }
            }.onFailure {
                updateProgress(repo.name, "")
                isDownloadError.value = true
                errorMessage = it.message.toString()
            }
        }
    }

    private fun updateProgress(repo: String, progress: String) {
        progressMap.value = progressMap.value.toMutableMap().apply {
            this[repo] = progress
        }
    }

    private fun saveRepository(repository: Repository) {
        viewModelScope.launch {
            databaseRepository.insertRepository(repository)
        }
    }
}