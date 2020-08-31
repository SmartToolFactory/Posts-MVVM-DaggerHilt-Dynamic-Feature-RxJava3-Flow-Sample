package com.smarttoolfactory.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smarttoolfactory.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface PostDaoRxJava3 {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postEntity: PostEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(postEntityList: List<PostEntity>): Completable

    @Delete
    fun deletePost(entity: PostEntity): Single<Int>

    @Query("DELETE FROM post")
    fun deleteAll(): Completable

    /**
     * Get number of posts in db
     */
    @Query("SELECT COUNT(*) FROM post")
    fun getPostCount(): Maybe<Int>

    /**
     * Get list of [PostEntity]s to from database.
     */
    @Query("SELECT * FROM post")
    fun getPostsSingle(): Single<List<PostEntity>>

    /**
     * Get list of [PostEntity]s to from database.
     */
    @Query("SELECT * FROM post")
    fun getPostsMaybe(): Maybe<List<PostEntity>>

    /**
     * Get list of [PostEntity]s to from database.
     */
    @Query("SELECT * FROM post")
    fun getPosts(): Observable<List<PostEntity>>

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

    /**
     * Update a post's favorite or display count status.
     */
    @Update
    fun updatePostFavoriteOrSelectStatus(postEntity: PostEntity)
}
