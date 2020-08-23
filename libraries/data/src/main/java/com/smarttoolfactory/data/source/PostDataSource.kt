package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.api.PostApi
import com.smarttoolfactory.data.db.PostDao
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface PostDataSource

/*
    Coroutines
 */

interface RemotePostDataSource : PostDataSource {
    suspend fun getPostDTOs(): List<PostDTO>
}

interface LocalPostDataSource : PostDataSource {
    suspend fun getPostEntities(): List<PostEntity>
    suspend fun saveEntities(posts: List<PostEntity>): List<Long>
    suspend fun deletePostEntities()
}


class RemotePostDataSourceImpl(private val postApi: PostApi) : RemotePostDataSource {

    override suspend fun getPostDTOs(): List<PostDTO> {
        return postApi.getPosts()
    }

}

class LocalPostDataSourceImpl(private val postDao: PostDao, private val cache: Cache) :
    LocalPostDataSource {

    override suspend fun getPostEntities(): List<PostEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun saveEntities(posts: List<PostEntity>): List<Long> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePostEntities() {
        TODO("Not yet implemented")
    }

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

/*
    Cache
 */
interface Cache {

    var expireDuration: Int

    fun saveCacheTime()

    fun getCacheSaveTime(): Long

}
