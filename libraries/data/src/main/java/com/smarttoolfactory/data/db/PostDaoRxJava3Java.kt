package com.smarttoolfactory.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.smarttoolfactory.data.model.PostEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


@Dao
interface PostDaoRxJava {

    @Insert
    fun insert(postEntity: PostEntity): Completable

    @Insert
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
     *
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
}
