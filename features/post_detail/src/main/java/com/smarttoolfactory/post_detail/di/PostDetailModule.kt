package com.smarttoolfactory.post_detail.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module(includes = [PostDetailBindModule::class])
class PostDetailModule

@InstallIn(FragmentComponent::class)
@Module
abstract class PostDetailBindModule {
    @Binds
    abstract fun bindContext(application: Application): Context
}
