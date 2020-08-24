package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.source.LocalPostDataSourceCoroutines
import com.smarttoolfactory.data.source.RemotePostDataSourceCoroutines
import javax.inject.Inject

/**
 * Repository for persistence layer. Local Data source acts as Single Source of Truth
 * even if data is retrieved from REST api it's stored to database and retrieved from database
 * when required.
 *
 * * Get [getPostEntitiesFromLocal] function returns data that is not expired, if data is older than
 * required it uses [LocalPostDataSourceCoroutines] to retrieve data.
 *
 * * Other than caching this class does not contain any business
 * logic to set order of retrieving, saving or deleting data operations.
 *
 * All business logic is delegated to a UseCase/Interactor class that injects
 * this [PostRepository] as dependency.
 *
 */
class PostRepositoryCoroutinesImpl @Inject constructor(
    private val localPostDataSource: LocalPostDataSourceCoroutines,
    private val remotePostDataSource: RemotePostDataSourceCoroutines,
    private val mapper: DTOtoEntityMapper
) : PostRepository {

    override suspend fun fetchEntitiesFromRemote(): List<PostEntity> {
        val postDTOList = remotePostDataSource.getPostDTOs()
        return mapper.map(postDTOList)
    }

    override suspend fun getPostEntitiesFromLocal(): List<PostEntity> {
        return localPostDataSource.getPostEntities()
    }

    override suspend fun savePostEntities(postEntities: List<PostEntity>) {
        localPostDataSource.saveEntities(postEntities)
    }

    override suspend fun deletePostEntities() {
        localPostDataSource.deletePostEntities()
    }
}

class PostRepositoryRxJava3Impl
