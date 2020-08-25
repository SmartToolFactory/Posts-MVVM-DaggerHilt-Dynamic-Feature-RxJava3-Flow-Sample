package com.smarttoolfactory.domain.mapper

import com.smarttoolfactory.data.mapper.ListMapper
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
