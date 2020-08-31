package com.smarttoolfactory.core.di

import com.smarttoolfactory.core.CoreDependency
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
class CoreModule {

    @Provides
    fun provideCoreDependency() = CoreDependency()
}
