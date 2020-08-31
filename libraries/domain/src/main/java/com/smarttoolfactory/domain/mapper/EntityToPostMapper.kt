package com.smarttoolfactory.domain.mapper

import com.smarttoolfactory.data.mapper.ListMapper
import com.smarttoolfactory.data.model.PostAndStatus
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.domain.model.Post
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntityToPostMapper @Inject constructor() : ListMapper<PostEntity, Post> {
    override fun map(input: List<PostEntity>): List<Post> {
        return input.map {
            Post(it.id, it.userId, it.title, it.body)
        }
    }
}

class EntityToPostStatusMapper @Inject constructor() : ListMapper<PostAndStatus, Post> {
    override fun map(input: List<PostAndStatus>): List<Post> {

        return input.map {

            val postEntity = it.postEntity
            val status = it.postStatus

            Post(
                postEntity.id,
                postEntity.userId,
                postEntity.title,
                postEntity.body,
                status?.displayCount ?: 0,
                status?.isFavorite ?: false
            )
        }
    }
}
