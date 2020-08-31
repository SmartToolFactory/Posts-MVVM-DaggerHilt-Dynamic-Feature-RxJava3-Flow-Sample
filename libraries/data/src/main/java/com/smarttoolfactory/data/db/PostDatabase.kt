package com.smarttoolfactory.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.model.PostStatus

const val DATABASE_NAME = "post.db"

@Database(
    entities = [PostEntity::class, PostStatus::class],
    version = 2,
    exportSchema = true
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDaoCoroutines
    abstract fun postDaoRxJava(): PostDaoRxJava3
    abstract fun postStatusDao(): PostStatusDao
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {

        database.execSQL(
            "CREATE TABLE IF NOT EXISTS post_status " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "userAccountId INTEGER NOT NULL, " +
                "postId INTEGER NOT NULL, " +
                "displayCount INTEGER NOT NULL, " +
                "isFavorite INTEGER NOT NULL, " +
                "FOREIGN KEY(postId) REFERENCES post(id) " +
                "ON UPDATE NO ACTION ON DELETE NO ACTION )"
        )

        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_post_status_postId " +
                "ON post_status (userAccountId, postId)"
        )
    }
}
