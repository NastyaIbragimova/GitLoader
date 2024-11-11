package com.example.gitloader.domain.mappers

import com.example.gitloader.domain.models.Repository
import com.example.gitloader.data.UserReposResponse
import com.example.gitloader.data.utils.DEFAULT_BRANCH

fun UserReposResponse.toRepository(): Repository {
    return Repository(
        id = this.id,
        name = this.name,
        description = this.description,
        htmlUrl = this.htmlUrl,
        language = this.language,
        defaultBranch = this.defaultBranch ?: DEFAULT_BRANCH,
        owner = this.owner.login
    )
}

fun List<UserReposResponse>.toRepositoryList(): List<Repository> {
    return this.map { it.toRepository() }
}