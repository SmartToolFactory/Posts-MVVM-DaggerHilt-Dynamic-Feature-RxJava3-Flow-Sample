package com.smarttoolfactory.home.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.home.viewmodel.PostStatusViewModel
import com.smarttoolfactory.home.viewmodel.PostStatusViewModelFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@InstallIn(FragmentComponent::class)
@Module
class PostListModule {

    @Provides
    fun providePostListViewModel(fragment: Fragment, factory: PostStatusViewModelFactory) =
        ViewModelProvider(fragment, factory).get(PostStatusViewModel::class.java)

    @Provides
    fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
}
