package com.example.gitloader.presentation.downloads

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gitloader.R
import com.example.gitloader.data.utils.DOWNLOADS_URI_NEW
import com.example.gitloader.data.utils.DOWNLOADS_URI_OLD
import com.example.gitloader.data.utils.FileDownloader
import com.example.gitloader.presentation.items.RepositoryItem

@Composable
fun DownloadsScreen() {
    val viewModel = hiltViewModel<DownloadsViewModel>()
    val savedRepositories by viewModel.savedRepositories.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.downloads_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (savedRepositories.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.downloads_screen_empty_list),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            LazyColumn {
                items(savedRepositories) { repo ->
                    RepositoryItem(
                        repository = repo,
                        onFileClick = {
                            openDownloadsFolder(context)
                        },
                        onOpenInBrowserClick = {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                            context.startActivity(browserIntent)
                        },
                        downloadStatus = FileDownloader.FILE_DOWNLOADED
                    )
                }
            }
        }
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