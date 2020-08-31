package com.smarttoolfactory.data.source

import com.smarttoolfactory.data.db.PostDaoCoroutines
import com.smarttoolfactory.data.db.PostStatusDao
import com.smarttoolfactory.data.model.PostAndStatus
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.model.PostStatus
import javax.inject.Inject

interface LocalPostStatusSource : PostDataSource {
    suspend fun getPostWithStatus(): List<PostAndStatus>
    suspend fun updatePostStatus(postStatus: PostStatus)
    suspend fun getPostStatus(userAccountId: Int, postId: Int): PostStatus?
    suspend fun savePostEntities(postEntities: List<PostEntity>)
    suspend fun deletePostEntities()
}

class LocalPostStatusSourceImpl @Inject constructor(
    private val postDaoCoroutines: PostDaoCoroutines,
    private val postStatusDao: PostStatusDao,
) :
    LocalPostStatusSource {

    override suspend fun getPostWithStatus(): List<PostAndStatus> {
        return postStatusDao.getPostWithStatus()
    }

    override suspend fun updatePostStatus(postStatus: PostStatus) {
        postStatusDao.insertOrUpdatePostStatus(postStatus)
    }

    override suspend fun getPostStatus(userAccountId: Int, postId: Int): PostStatus? {
        return postStatusDao.getUpdateStatus(userAccountId, postId)
    }

    override suspend fun savePostEntities(postEntities: List<PostEntity>) {
        postDaoCoroutines.insert(postEntities)
    }

    override suspend fun deletePostEntities() {
        postDaoCoroutines.deleteAll()
    }
}
