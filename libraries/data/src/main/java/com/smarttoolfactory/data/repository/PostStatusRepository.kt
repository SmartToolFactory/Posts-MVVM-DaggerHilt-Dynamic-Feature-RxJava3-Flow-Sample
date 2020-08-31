package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.model.PostAndStatus
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.model.PostStatus
import com.smarttoolfactory.data.source.LocalPostStatusSource
import com.smarttoolfactory.data.source.RemotePostDataSourceCoroutines
import javax.inject.Inject

interface PostStatusRepository {

    suspend fun fetchEntitiesFromRemote(): List<PostEntity>

    suspend fun getPostWithStatusFromLocal(): List<PostAndStatus>

    suspend fun updatePostStatus(postStatus: PostStatus): Int

    suspend fun savePostEntities(postEntities: List<PostEntity>)

    suspend fun deletePostEntities()
}

class PostStatusRepositoryImpl
@Inject constructor(
    private val remotePostDataSource: RemotePostDataSourceCoroutines,
    private val localPostDataSource: LocalPostStatusSource,
    private val mapper: DTOtoEntityMapper
) :
    PostStatusRepository {

    override suspend fun fetchEntitiesFromRemote(): List<PostEntity> {
        val postDTOList = remotePostDataSource.getPostDTOs()
        return mapper.map(postDTOList)
    }

    override suspend fun getPostWithStatusFromLocal(): List<PostAndStatus> {
        return localPostDataSource.getPostWithStatus()
    }

    override suspend fun updatePostStatus(postStatus: PostStatus): Int {
        return localPostDataSource.updatePostStatus(postStatus)
    }

    override suspend fun savePostEntities(postEntities: List<PostEntity>) {
        localPostDataSource.savePostEntities(postEntities)
    }

    override suspend fun deletePostEntities() {
        localPostDataSource.deletePostEntities()
    }
}
