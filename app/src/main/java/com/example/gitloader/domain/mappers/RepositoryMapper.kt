package com.example.gitloader.domain.mappers

import com.example.gitloader.data.utils.DEFAULT_BRANCH
import com.example.gitloader.db.RepositoryEntity
import com.example.gitloader.domain.models.Repository

fun RepositoryEntity.fromEntity(): Repository {
    return Repository(
        id = this.id,
        name = this.name,
        description = this.description,
        htmlUrl = this.htmlUrl,
        defaultBranch = this.defaultBranch ?: DEFAULT_BRANCH,
        language = this.language,
        owner = this.owner
    )
}

fun List<RepositoryEntity>.toRepositoryList(): List<Repository> {
    return this.map { it.fromEntity() }
}

fun Repository.toEntity(): RepositoryEntity {
    return RepositoryEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        htmlUrl = this.htmlUrl,
        defaultBranch = this.defaultBranch,
        language = this.language,
        owner = this.owner
    )
}