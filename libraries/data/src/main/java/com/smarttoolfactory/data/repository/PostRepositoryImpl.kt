package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.source.LocalPostDataSourceCoroutines
import com.smarttoolfactory.data.source.RemotePostDataSourceCoroutines

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
class PostRepositoryCoroutinesImpl(
    private val localPostDataSource: LocalPostDataSourceCoroutines,
    private val remotePostDataSource: RemotePostDataSourceCoroutines,
    private val mapper: DTOtoEntityMapper
) : PostRepository {

    override suspend fun getPostEntitiesFromLocal(): List<PostEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchEntitiesFromRemote(): List<PostEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun savePostEntities(postEntities: List<PostEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePostEntities() {
        TODO("Not yet implemented")
    }
}

class PostRepositoryRxJava3Impl
