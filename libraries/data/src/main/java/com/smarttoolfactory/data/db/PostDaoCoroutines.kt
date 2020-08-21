package com.smarttoolfactory.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import com.smarttoolfactory.data.model.PostEntity
import kotlinx.coroutines.flow.Flow


const val DATABASE_NAME = "post.db"

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postDaoRxJava(): PostDaoRxJava
}

@Dao
interface PostDao {

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
    // Suspend
    @Query("SELECT * FROM post")
    suspend fun getPostList(): List<PostEntity>

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

