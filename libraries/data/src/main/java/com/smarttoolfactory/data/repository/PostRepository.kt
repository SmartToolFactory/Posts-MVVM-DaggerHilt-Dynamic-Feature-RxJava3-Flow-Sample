package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.model.PostEntity

/**
 * This repository contains no data save, delete or fetch logic with Coroutines.
 *
 * All business logic for creating offline-first or offline-last approach is moved to UseCase
 */
interface PostRepository {

    suspend fun fetchEntitiesFromRemote(): List<PostEntity>

    suspend fun getPostEntitiesFromLocal(): List<PostEntity>

    suspend fun savePostEntities(postEntities: List<PostEntity>)

    suspend fun deletePostEntities()
}
