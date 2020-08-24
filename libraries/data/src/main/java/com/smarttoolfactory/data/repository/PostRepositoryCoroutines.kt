package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

/**
 * This repository contains no data save, delete or fetch logic with Coroutines.
 *
 * All business logic for creating offline-first or offline-last approach is moved to UseCase
 */
interface PostRepositoryCoroutines {

    suspend fun fetchEntitiesFromRemote(): List<PostEntity>

    suspend fun getPostEntitiesFromLocal(): List<PostEntity>

    suspend fun savePostEntities(postEntities: List<PostEntity>)

    suspend fun deletePostEntities()
}

/**
 * This repository contains no data save, delete or fetch logic with RxJava3.
 *
 * All business logic for creating offline-first or offline-last approach is moved to UseCase
 */
interface PostRepositoryRxJava3 {

    fun fetchEntitiesFromRemote(): Single<List<PostEntity>>

    fun getPostEntitiesFromLocal(): Single<List<PostEntity>>

    fun savePostEntities(postEntities: List<PostEntity>): Completable

    fun deletePostEntities(): Completable
}
