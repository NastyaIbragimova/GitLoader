package com.example.gitloader.data

import com.example.gitloader.domain.NetworkRepository
import com.example.gitloader.domain.mappers.toRepositoryList
import com.example.gitloader.domain.models.Repository
import com.example.gitloader.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : NetworkRepository {

    override suspend fun getRepos(username: String): Result<List<Repository>?> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRepos(username)
                Result.success(response)
                if (response.isSuccessful) {
                    Result.success(response.body()?.toRepositoryList())
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    override suspend fun downloadZip(owner: String,repo: String,ref: String): Result<ResponseBody?> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.downloadZip(owner, repo, ref)
                if (response.isSuccessful) {
                    Result.success(response.body())
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}