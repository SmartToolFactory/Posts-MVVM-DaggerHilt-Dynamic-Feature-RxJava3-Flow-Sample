package com.smarttoolfactory.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smarttoolfactory.data.model.PostEntity

const val DATABASE_NAME = "post.db"

@Database(
    entities = [PostEntity::class],
    version = 2,
    exportSchema = true
)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDaoCoroutines
    abstract fun postDaoRxJava(): PostDaoRxJava3
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE post ADD COLUMN visitCount INTEGER NOT NULL DEFAULT 0"
        )

        database.execSQL(
            "ALTER TABLE post ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0"
        )
    }
}
