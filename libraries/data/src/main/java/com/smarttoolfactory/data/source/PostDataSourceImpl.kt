package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.api.PostApi
import com.smarttoolfactory.data.api.PostApiRxJava
import com.smarttoolfactory.data.db.PostDaoCoroutines
import com.smarttoolfactory.data.db.PostDaoRxJava3
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

/*
    *** Coroutines Implementations for PostDataSources ***
 */
class RemotePostDataSourceCoroutinesImpl @Inject constructor(private val postApi: PostApi) :
    RemotePostDataSourceCoroutines {

    override suspend fun getPostDTOs(): List<PostDTO> {
        return postApi.getPosts()
    }
}

class LocalPostDataSourceCoroutinesImpl
@Inject constructor(private val postDao: PostDaoCoroutines) : LocalPostDataSourceCoroutines {

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
    *** RxJava3 Implementations for PostDataSources ***
 */
class RemoteDataSourceRxJava3Impl @Inject constructor(private val postApi: PostApiRxJava) :
    RemotePostDataSourceRxJava3 {

    override fun getPostDTOs(): Single<List<PostDTO>> {
        return postApi.getPostsSingle()
    }
}

class LocalDataSourceRxJava3Impl @Inject constructor(private val postDaoRxJava3: PostDaoRxJava3) :
    LocalPostDataSourceRxJava3 {

    override fun getPostEntities(): Single<List<PostEntity>> {
        return postDaoRxJava3.getPostsSingle()
    }

    override fun saveEntities(posts: List<PostEntity>): Completable {
        return postDaoRxJava3.insert(posts)
    }

    override fun deletePostEntities(): Completable {
        return postDaoRxJava3.deleteAll()
    }
}
