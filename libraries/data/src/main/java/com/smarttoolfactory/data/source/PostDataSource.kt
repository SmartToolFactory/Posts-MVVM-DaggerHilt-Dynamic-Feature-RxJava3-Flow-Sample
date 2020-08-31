package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface PostDataSource

/*
    Coroutines
 */
interface RemotePostDataSourceCoroutines : PostDataSource {
    suspend fun getPostDTOs(): List<PostDTO>
}

interface LocalPostDataSourceCoroutines : PostDataSource {
    suspend fun getPostEntities(): List<PostEntity>
    suspend fun saveEntities(posts: List<PostEntity>): List<Long>
    suspend fun deletePostEntities()
}

/*
    RxJava3
 */
interface RemotePostDataSourceRxJava3 : PostDataSource {
    fun getPostDTOs(): Single<List<PostDTO>>
}

interface LocalPostDataSourceRxJava3 : PostDataSource {
    fun getPostEntities(): Single<List<PostEntity>>
    fun saveEntities(posts: List<PostEntity>): Completable
    fun deletePostEntities(): Completable
}
