package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.model.PostAndStatus
import com.smarttoolfactory.data.model.PostStatus
import com.smarttoolfactory.data.repository.PostStatusRepository
import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
import com.smarttoolfactory.domain.error.EmptyDataException
import com.smarttoolfactory.domain.mapper.EntityToPostStatusMapper
import com.smarttoolfactory.domain.model.Post
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * 🔥 UseCase for getting Post list with offline first or offline last approach and with status
 * such as how many times details displayed and if it's liked. This useCase is same as
 * [GetPostListUseCaseFlow] other than it gets the results and status of the posts.
 *
 * * Different than [GetPostListUseCaseFlow] this useCase has ability to update [PostStatus] with
 * [GetPostsWithStatusUseCaseFlow.updatePostStatus] function.
 *
 */
class GetPostsWithStatusUseCaseFlow @Inject constructor(
    private val postRepository: PostStatusRepository,
    private val entityToPostMapper: EntityToPostStatusMapper,
    private val dispatcherProvider: UseCaseDispatchers
) {

    fun updatePostStatus(post: Post): Flow<Unit> {

        return flow {
            emit(
                postRepository.getPostStatus(
                    userAccountId = -1,
                    postId = post.id
                )
            )
        }
            .map { postStatus ->

                // There is a Post status saved previously
                val newStatus = if (postStatus != null) {
                    PostStatus(
                        id = postStatus.id,
                        postId = postStatus.postId,
                        userAccountId = postStatus.userAccountId,
                        displayCount = post.displayCount,
                        isFavorite = post.isFavorite
                    )
                } else {
                    PostStatus(
                        postId = post.id,
                        userAccountId = -1,
                        displayCount = post.displayCount,
                        isFavorite = post.isFavorite
                    )
                }

                postRepository.updatePostStatus(newStatus)
            }
    }

    fun getPostFlowOfflineLast(): Flow<List<Post>> {
        return flow { emit(postRepository.fetchEntitiesFromRemote()) }
            .map {
                if (it.isNullOrEmpty()) {
                    throw EmptyDataException("No Data is available in Remote source!")
                } else {
                    postRepository.run {
                        deletePostEntities()
                        savePostEntities(it)
                        getPostWithStatusFromLocal()
                    }
                }
            }
            .flowOn(dispatcherProvider.ioDispatcher)
            // This is where remote exception or least likely db exceptions are caught
            .catch { throwable ->
                emitAll(flowOf(postRepository.getPostWithStatusFromLocal()))
            }
            .map {
                toPostListOrError(it)
            }
    }

    fun getPostFlowOfflineFirst(): Flow<List<Post>> {
        return flow { emit(postRepository.getPostWithStatusFromLocal()) }
            .catch { throwable ->
                emitAll(flowOf(listOf()))
            }
            .map {
                if (it.isEmpty()) {
                    postRepository.run {
                        val data = fetchEntitiesFromRemote()
                        deletePostEntities()
                        savePostEntities(data)
                        getPostWithStatusFromLocal()
                    }
                } else {
                    it
                }
            }
            .flowOn(dispatcherProvider.ioDispatcher)
            .catch { throwable ->
                emitAll(flowOf(listOf()))
            }
            .map {
                toPostListOrError(it)
            }
    }

    private fun toPostListOrError(postEntityList: List<PostAndStatus>): List<Post> {
        return if (!postEntityList.isNullOrEmpty()) {
            entityToPostMapper.map(postEntityList)
        } else {
            throw EmptyDataException("Empty data mapping error!")
        }
    }
}
