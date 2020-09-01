package com.smarttoolfactory.post_detail.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.post_detail.PostDetailViewModel
import com.smarttoolfactory.post_detail.PostDetailViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@InstallIn(FragmentComponent::class)
@Module
class PostDetailModule {

    @Provides
    fun providePostDetailViewModel(fragment: Fragment, factory: PostDetailViewModelFactory) =
        ViewModelProvider(fragment, factory).get(PostDetailViewModel::class.java)

    @Provides
    fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
}
