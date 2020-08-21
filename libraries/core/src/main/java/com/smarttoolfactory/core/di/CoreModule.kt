package com.smarttoolfactory.core.di

import com.smarttoolfactory.core.CoreDependency
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class CoreModule {

    @Singleton
    @Provides
    fun provideCoreDependency() = CoreDependency()
}
