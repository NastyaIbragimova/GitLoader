package com.example.gitloader.presentation.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gitloader.R
import com.example.gitloader.data.utils.DOWNLOADS_URI_NEW
import com.example.gitloader.data.utils.DOWNLOADS_URI_OLD
import com.example.gitloader.data.utils.FileDownloader
import com.example.gitloader.domain.models.Repository
import com.example.gitloader.presentation.items.RepositoryItem
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchScreen() {
    val viewModel = hiltViewModel<SearchViewModel>()
    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    if (viewModel.isFileDownloaded.value) {
        viewModel.isFileDownloaded.value = false
        Toast.makeText(
            LocalContext.current,
            stringResource(R.string.search_screen_file_downloaded), Toast.LENGTH_LONG
        ).show()
    }

    if (viewModel.isDownloadError.value) {
        viewModel.isDownloadError.value = false
        Toast.makeText(
            LocalContext.current,
            stringResource(R.string.search_screen_error_downloading_file, viewModel.errorMessage),
            Toast.LENGTH_LONG
        ).show()
    }

    val repositories by viewModel.repoResponse
    val searchQuery by viewModel.searchQuery
    val isSearchPerformed by viewModel.isSearchPerformed
    val isLoading by viewModel.isLoading
    val isError by viewModel.isError
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query -> viewModel.updateSearchQuery(query) },
            label = { Text(stringResource(R.string.search_screen_search_hint)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.getRepos(searchQuery)
                keyboardController?.hide()
            }),
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                viewModel.getRepos(searchQuery)
                keyboardController?.hide()
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(stringResource(R.string.search_screen_search_btn))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isError -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                else -> {
                    if (isSearchPerformed && repositories.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_screen_no_repo_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        RepositoryList(repositories = repositories, viewModel, permissionState)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RepositoryList(
    repositories: List<Repository>,
    viewModel: SearchViewModel,
    permissionState: MultiplePermissionsState
) {
    val context = LocalContext.current
    val progressMap by viewModel.progressMap
    LazyColumn {
        items(repositories) { repo ->
            val progress = progressMap[repo.name] ?: ""
            RepositoryItem(
                repository = repo,
                onFileClick = {
                    if (repo.isDownloaded || progress == FileDownloader.FILE_DOWNLOADED) openDownloadsFolder(
                        context
                    )
                    else onDownload(viewModel, permissionState, repo, context)
                },
                onOpenInBrowserClick = {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                    context.startActivity(browserIntent)
                },
                downloadStatus = progress
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun onDownload(
    viewModel: SearchViewModel,
    permissionState: MultiplePermissionsState, repo: Repository, context: Context
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        if (permissionState.permissions.all { it.status.isGranted }) {
            viewModel.downloadZip(repo)
        } else {
            permissionState.launchMultiplePermissionRequest()
        }
    } else {
        viewModel.downloadZip(repo)
    }
}

private fun openDownloadsFolder(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data =
                Uri.parse(DOWNLOADS_URI_NEW)
        }
        context.startActivity(intent)
    } else {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val downloadsUri =
                Uri.parse(DOWNLOADS_URI_OLD)
            setDataAndType(downloadsUri, "resource/folder")
        }
        context.startActivity(intent)
    }
}