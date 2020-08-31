package com.smarttoolfactory.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smarttoolfactory.data.model.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDaoCoroutines {

    /*
        ***** Coroutines ******
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<PostEntity>): List<Long>

    @Delete
    suspend fun deletePost(entity: PostEntity): Int

    @Query("DELETE FROM post")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM post")
    suspend fun getPostCount(): Int

    /**
     * Get posts from database.
     *
     * *If database is empty returns empty list []
     */
    @Query("SELECT * FROM post")
    suspend fun getPostList(): List<PostEntity>

    /**
     * Get list of [PostEntity] that contains [keyword] in title or body of the post
     */
//    @Query("SELECT * FROM post WHERE title LIKE '%' || :keyword  || '%' OR  body LIKE '%' || :keyword  || '%'")
//    suspend fun searchPostsByKeyword(keyword: String): List<PostEntity>
//
//    /**
//     * Search [PostEntity] that belong to user with [posterId]
//     */
//    @Query("SELECT * FROM post WHERE userId =:posterId")
//    suspend fun searchPostsByUser(posterId: Int)
//
//    /**
//     * Get most displayed posts in descending order
//     */
//    @Query("SELECT * FROM post ORDER BY displayCount DESC")
//    suspend fun getDisplayedMostPosts(): List<PostEntity>

    /*
        ***** Flow ******
    */
    /**
     * Get [Flow] of [List] of [PostEntity]
     *
     * * If database is empty returns an empty list []
     */
    // Flow
    @Query("SELECT * FROM post")
    fun getPostListFlow(): Flow<List<PostEntity>>
}
