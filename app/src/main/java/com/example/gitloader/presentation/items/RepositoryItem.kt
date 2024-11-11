package com.example.gitloader.presentation.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gitloader.R
import com.example.gitloader.data.utils.FileDownloader
import com.example.gitloader.domain.models.Repository

@Composable
fun RepositoryItem(
    repository: Repository,
    onFileClick: () -> Unit,
    onOpenInBrowserClick: () -> Unit,
    downloadStatus: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repository.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                repository.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                repository.language?.let { language ->
                    Text(
                        text = stringResource(R.string.repository_item_language, language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = stringResource(R.string.repository_item_owner, repository.owner),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.repository_item_open_in_browser),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onOpenInBrowserClick() }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onFileClick,
                modifier = Modifier.align(Alignment.Bottom),
                enabled = downloadStatus != FileDownloader.FILE_DOWNLOADING,
            ) {
                when {
                    downloadStatus == FileDownloader.FILE_DOWNLOADING -> {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    }

                    repository.isDownloaded || downloadStatus == FileDownloader.FILE_DOWNLOADED -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_folder),
                            contentDescription = stringResource(R.string.repository_item_open_in_folder),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    else -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = stringResource(R.string.repository_item_download_archive),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}