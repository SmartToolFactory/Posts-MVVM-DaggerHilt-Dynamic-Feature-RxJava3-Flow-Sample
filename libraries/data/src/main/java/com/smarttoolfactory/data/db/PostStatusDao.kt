package com.smarttoolfactory.data.db

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.smarttoolfactory.data.model.PostAndStatus
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.model.PostStatus

@Dao
interface PostStatusDao {

    /**
     * Update a post's favorite or display count status.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePostStatus(postStatus: PostStatus)

    /**
     * Get status belong to current user for this [PostEntity] with [PostEntity.id]
     */
    @Query("SELECT * FROM post_status WHERE userAccountId =:userId AND postId =:postId")
    suspend fun getUpdateStatus(userId: Int, postId: Int): PostStatus?

    /**
     * This method uses [Embedded] and [Relation] annotations to create a class that contains
     * [PostEntity] and [PostStatus] which is display count and like belong to this [PostEntity]
     * by a specific account that is currently logged in
     */
    @Transaction
    @Query("SELECT * FROM post")
    suspend fun getPostWithStatus(): List<PostAndStatus>
}
