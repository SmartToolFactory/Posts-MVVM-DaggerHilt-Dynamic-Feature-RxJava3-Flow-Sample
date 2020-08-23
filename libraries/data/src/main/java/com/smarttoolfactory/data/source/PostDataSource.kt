package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.api.PostApi
import com.smarttoolfactory.data.api.PostApiRxJava
import com.smarttoolfactory.data.db.PostDao
import com.smarttoolfactory.data.db.*
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

class LocalPostDataSourceImpl(private val postDao: PostDao) :
    LocalPostDataSource {

    override suspend fun getPostEntities(): List<PostEntity> {
        return postDao.getPostList()
    }

    override suspend fun saveEntities(posts: List<PostEntity>): List<Long> {

        return postDao.insert(posts)
    }

    override suspend fun deletePostEntities() {
        postDao.deleteAll()
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

class RemoteDataSourceRxJava3Impl(private val postApi: PostApiRxJava) :
    RemotePostDataSourceRxJava3 {

    override fun getPostDTOs(): Single<List<PostDTO>> {
        return postApi.getPostsSingle()
    }
}


