package com.example.gitloader.data

import android.os.Environment
import com.example.gitloader.db.RepositoryDao
import com.example.gitloader.domain.DatabaseRepository
import com.example.gitloader.domain.mappers.toEntity
import com.example.gitloader.domain.mappers.toRepositoryList
import com.example.gitloader.domain.models.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val repositoryDao: RepositoryDao
) : DatabaseRepository {

    override suspend fun getAllRepositories(): Flow<List<Repository>> {
        return repositoryDao.getAllRepositories()
            .map { entities ->
                val repositories = entities.toRepositoryList()
                checkDownloadedRepositories(repositories)
                repositories
            }
    }

    override suspend fun deleteRepository(repository: Repository) {
        withContext(Dispatchers.IO) { repositoryDao.deleteRepository(repository.toEntity()) }
    }

    override suspend fun insertRepository(repository: Repository) {
        withContext(Dispatchers.IO) { repositoryDao.insertRepository(repository.toEntity()) }
    }

    private suspend fun checkDownloadedRepositories(repositories: List<Repository>) {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        repositories.forEach { repository ->
            val file =
                File(downloadsDir, "${repository.name}-${repository.defaultBranch}.zip")
            if (!file.exists()) {
                deleteRepository(repository)
            }
        }
    }
}