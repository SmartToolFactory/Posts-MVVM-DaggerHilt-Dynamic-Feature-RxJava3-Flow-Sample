package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.repository.PostRepositoryRxJava3
import com.smarttoolfactory.domain.error.EmptyDataException
import com.smarttoolfactory.domain.mapper.EntityToPostMapper
import com.smarttoolfactory.domain.model.Post
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class GetPostListUseCaseRxJava3 @Inject constructor(
    private val repository: PostRepositoryRxJava3,
    private val entityToPostMapper: EntityToPostMapper,
) {

    /**
     * Function to retrieve data from repository with offline-last which checks
     * REMOTE data source first.
     *
     * * Check out Remote Source first
     * * If empty data or null returned throw empty set exception
     * * If error occurred while fetching data from remote: Try to fetch data from db
     * * If data is fetched from remote source: delete old data, save new data and return new data
     * * If both network and db don't have any data throw empty set exception
     *
     */
    fun getPostsOfflineLast(): Single<List<Post>> {

        return repository.fetchEntitiesFromRemote()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .flatMap {
                if (it.isNullOrEmpty()) {
                    throw EmptyDataException("No Data is available in remote source!")
                } else {
                    repository.deletePostEntities()
                        .andThen(repository.savePostEntities(it))
                        .andThen(repository.getPostEntitiesFromLocal())
                }
            }
            .onErrorResumeNext { throwable ->
                repository.getPostEntitiesFromLocal()
            }
            .toPostListOrError()
    }

    /**
     * Function to retrieve data from repository with offline-first which checks
     * LOCAL data source first.
     *
     * * Check out Local Source first
     * * If empty data or null returned throw empty set exception
     * * If error occurred while fetching data from remote: Try to fetch data from db
     * * If data is fetched from remote source: delete old data, save new data and return new data
     * * If both network and db don't have any data throw empty set exception
     *
     */
    fun getPostsOfflineFirst(): Single<List<Post>> {

        return repository.getPostEntitiesFromLocal()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .onErrorResumeNext { cause ->
                Single.just(listOf())
            }
            .concatMap { list ->
                if (list.isNullOrEmpty()) {
                    repository.fetchEntitiesFromRemote()
                        .concatMap {
                            repository.deletePostEntities()
                                .andThen(repository.savePostEntities(it))
                                .andThen(Single.just(it))
                        }
                } else {
                    Single.just(list)
                }
            }
            .onErrorResumeNext { cause ->
                Single.just(listOf())
            }
            .toPostListOrError()
    }

    /**
     * Uses `if(postListEntity.isNotEmpty) entityToPostMapper.map(it)  else Single.error`
     * with RxJava to map [PostEntity] list to [Post]
     */
    private fun Single<List<PostEntity>>.toPostListOrError(): Single<List<Post>> {
        return this
            .filter { it.isNotEmpty() }
            .map { entityToPostMapper.map(it) }
            .switchIfEmpty(
                Single.error(EmptyDataException("Data is not available in any source!"))
            )
    }
}
