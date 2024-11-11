package com.example.gitloader.data

import com.google.gson.annotations.SerializedName

data class UserReposResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("owner")
    val owner: Owner,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("default_branch")
    val defaultBranch: String?,

    @SerializedName("language")
    val language: String?,
)

data class Owner(
    @SerializedName("login")
    val login: String,

    @SerializedName("id")
    val id: Int?,
)