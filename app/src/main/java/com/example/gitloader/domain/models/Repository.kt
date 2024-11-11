package com.example.gitloader.domain.models

data class Repository(
    val id: Int,
    val name: String,
    val description: String?,
    val htmlUrl: String,
    val owner: String,
    val defaultBranch: String,
    val language: String?,
    val isDownloaded: Boolean = false
)