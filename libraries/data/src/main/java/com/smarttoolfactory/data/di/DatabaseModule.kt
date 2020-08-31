package com.smarttoolfactory.data.di

import android.app.Application
import androidx.room.Room
import com.smarttoolfactory.data.db.DATABASE_NAME
import com.smarttoolfactory.data.db.MIGRATION_1_2
import com.smarttoolfactory.data.db.PostDaoCoroutines
import com.smarttoolfactory.data.db.PostDaoRxJava3
import com.smarttoolfactory.data.db.PostDatabase
import com.smarttoolfactory.data.db.PostStatusDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(application: Application): PostDatabase {
        return Room.databaseBuilder(
            application,
            PostDatabase::class.java,
            DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Singleton
    @Provides
    fun providePostDao(appDatabase: PostDatabase): PostDaoCoroutines = appDatabase.postDao()

    @Provides
    fun providePostDaoRxJava3(appDatabase: PostDatabase): PostDaoRxJava3 =
        appDatabase.postDaoRxJava()

    @Provides
    fun providePostStatusDao(appDatabase: PostDatabase): PostStatusDao = appDatabase.postStatusDao()
}
