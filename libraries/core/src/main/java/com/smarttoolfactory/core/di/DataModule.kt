package com.smarttoolfactory.core.di

import com.smarttoolfactory.data.di.DatabaseModule
import com.smarttoolfactory.data.di.NetworkModule
import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.repository.PostRepositoryCoroutines
import com.smarttoolfactory.data.repository.PostRepositoryCoroutinesImpl
import com.smarttoolfactory.data.repository.PostRepositoryRxJava3
import com.smarttoolfactory.data.repository.PostRepositoryRxJava3Impl
import com.smarttoolfactory.data.repository.PostStatusRepository
import com.smarttoolfactory.data.repository.PostStatusRepositoryImpl
import com.smarttoolfactory.data.source.LocalDataSourceRxJava3Impl
import com.smarttoolfactory.data.source.LocalPostDataSourceCoroutines
import com.smarttoolfactory.data.source.LocalPostDataSourceCoroutinesImpl
import com.smarttoolfactory.data.source.LocalPostDataSourceRxJava3
import com.smarttoolfactory.data.source.LocalPostStatusSource
import com.smarttoolfactory.data.source.LocalPostStatusSourceImpl
import com.smarttoolfactory.data.source.RemoteDataSourceRxJava3Impl
import com.smarttoolfactory.data.source.RemotePostDataSourceCoroutines
import com.smarttoolfactory.data.source.RemotePostDataSourceCoroutinesImpl
import com.smarttoolfactory.data.source.RemotePostDataSourceRxJava3
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module(includes = [DataProviderModule::class, NetworkModule::class, DatabaseModule::class])
interface DataModule {

    /*
        Coroutines
     */
    @Singleton
    @Binds
    fun bindRemoteDataSourceCoroutines(remoteDataSource: RemotePostDataSourceCoroutinesImpl):
        RemotePostDataSourceCoroutines

    @Singleton
    @Binds
    fun bindLocalDataSourceCoroutines(localDataSource: LocalPostDataSourceCoroutinesImpl):
        LocalPostDataSourceCoroutines

    @Singleton
    @Binds
    fun bindRepositoryCoroutines(repository: PostRepositoryCoroutinesImpl):
        PostRepositoryCoroutines

    /*
        RxJava
     */
    @Singleton
    @Binds
    fun bindRemoteDataSourceRxJava3(remoteDataSource: RemoteDataSourceRxJava3Impl):
        RemotePostDataSourceRxJava3

    @Singleton
    @Binds
    fun bindLocalDataSourceRxJava3(localDataSource: LocalDataSourceRxJava3Impl):
        LocalPostDataSourceRxJava3

    @Singleton
    @Binds
    fun bindRepositoryRxJava3(repository: PostRepositoryRxJava3Impl):
        PostRepositoryRxJava3

    /*
        Post Status
     */
    @Singleton
    @Binds
    fun bindLocalPostStatusSource(localDataSource: LocalPostStatusSourceImpl):
        LocalPostStatusSource

    @Singleton
    @Binds
    fun bindPostStatusRepository(repository: PostStatusRepositoryImpl):
        PostStatusRepository
}

/**
 * This module is for injections with @Provides annotation
 */
@Module
@InstallIn(ApplicationComponent::class)
object DataProviderModule {
    @Provides
    fun provideDTOtoEntityMapper() = DTOtoEntityMapper()
}
