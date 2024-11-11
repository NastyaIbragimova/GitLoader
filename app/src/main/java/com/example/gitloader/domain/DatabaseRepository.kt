package com.example.gitloader.domain

import com.example.gitloader.domain.models.Repository
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {
    suspend fun insertRepository(repository: Repository)

    suspend fun getAllRepositories(): Flow<List<Repository>>

    suspend fun deleteRepository(repository: Repository)
}