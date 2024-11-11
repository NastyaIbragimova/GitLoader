package com.example.gitloader.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val owner: String,
    val defaultBranch: String?,
    val language: String?
)