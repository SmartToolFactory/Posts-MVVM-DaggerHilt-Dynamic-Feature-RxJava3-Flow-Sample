package com.smarttoolfactory.data.mapper

import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity

/**
 * Mapper for transforming objects between REST and database or REST/db and domain
 *  which are Non-nullable to Non-nullable
 */
interface Mapper<I, O> {
    fun map(input: I): O
}

/**
 * Mapper for transforming objects between REST and database or REST/db and domain
 * as [List] which are Non-nullable to Non-nullable
 */
interface ListMapper<I, O> : Mapper<List<I>, List<O>>


class DTOtoEntityMapper : ListMapper<PostDTO, PostEntity> {

    override fun map(input: List<PostDTO>): List<PostEntity> {
        return input.map {
            PostEntity(it.id, it.userId, it.title, it.body)
        }
    }
}
