package com.example.gitloader.network

import com.example.gitloader.data.UserReposResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("/users/{username}/repos")
    suspend fun getRepos(@Path("username") username: String): Response<List<UserReposResponse>>

    @GET("/repos/{owner}/{repo}/zipball/{ref}")
    suspend fun downloadZip(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("ref") ref: String
    ): Response<ResponseBody>

}