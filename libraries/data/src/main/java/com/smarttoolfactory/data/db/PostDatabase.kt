package com.smarttoolfactory.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smarttoolfactory.data.model.PostAttention
import com.smarttoolfactory.data.model.PostEntity

const val DATABASE_NAME = "post.db"

@Database(
    entities = [PostEntity::class, PostAttention::class],
    version = 1,
    exportSchema = true
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDaoCoroutines
    abstract fun postDaoRxJava(): PostDaoRxJava3
}

/*
val MIGRATION_1_2: Migration = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE post ADD COLUMN displayCount INTEGER NOT NULL DEFAULT 0"
        )

        database.execSQL(
            "ALTER TABLE post ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0"
        )
    }
 }
 */
