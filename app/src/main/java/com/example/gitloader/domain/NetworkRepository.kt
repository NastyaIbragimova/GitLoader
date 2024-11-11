package com.example.gitloader.domain

import com.example.gitloader.domain.models.Repository
import okhttp3.ResponseBody

interface NetworkRepository {
    suspend fun getRepos(username: String): Result<List<Repository>?>
    suspend fun downloadZip(owner: String, repo: String, ref: String): Result<ResponseBody?>
}