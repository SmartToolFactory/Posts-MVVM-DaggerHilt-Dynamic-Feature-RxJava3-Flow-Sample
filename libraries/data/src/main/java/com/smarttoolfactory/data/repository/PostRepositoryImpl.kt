package com.smarttoolfactory.data.repository

import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.source.LocalPostDataSource
import com.smarttoolfactory.data.source.LocalPostDataSourceRxJava3
import com.smarttoolfactory.data.source.RemotePostDataSource
import com.smarttoolfactory.data.source.RemotePostDataSourceRxJava3
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

/**
 * Repository for persistence layer. Local Data source acts as Single Source of Truth
 * even if data is retrieved from REST api it's stored to database and retrieved from database
 * when required.
 *
 * * Get [getPostEntitiesFromLocal] function returns data that is not expired, if data is older than
 * required it uses [LocalPostDataSource] to retrieve data.
 *
 * * Other than caching this class does not contain any business
 * logic to set order of retrieving, saving or deleting data operations.
 *
 * All business logic is delegated to a UseCase/Interactor class that injects
 * this [PostRepository] as dependency.
 *
 */
class PostRepositoryCoroutinesImpl(
    private val localPostDataSource: LocalPostDataSource,
    private val remotePostDataSource: RemotePostDataSource,
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

class PostRepoRxJava3Impl(
    private val localPostDataSource: LocalPostDataSourceRxJava3,
    private val remotePostDataSource: RemotePostDataSourceRxJava3,
    private val mapper: DTOtoEntityMapper
) : PostRepositoryRxJava3 {

    override fun fetchEntitiesFromRemote(): Single<List<PostEntity>> {
        return remotePostDataSource.getPostDTOs()
            .map {
                mapper.map(it)
            }
    }

    override fun getPostEntitiesFromLocal(): Single<List<PostEntity>> {
        return localPostDataSource.getPostEntities()
    }

    override fun savePostEntities(postEntities: List<PostEntity>): Completable {
        return localPostDataSource.saveEntities(postEntities)
    }

    override fun deletePostEntities(): Completable {
        return localPostDataSource.deletePostEntities()
    }
}
