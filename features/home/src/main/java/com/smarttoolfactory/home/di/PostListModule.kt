package com.smarttoolfactory.home.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.home.viewmodel.PostListFlowViewModelFactory
import com.smarttoolfactory.home.viewmodel.PostListRxJava3ViewModelFactory
import com.smarttoolfactory.home.viewmodel.PostListViewModelFlow
import com.smarttoolfactory.home.viewmodel.PostListViewModelRxJava3
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
    fun providePostListViewModelFlow(fragment: Fragment, factory: PostListFlowViewModelFactory) =
        ViewModelProvider(fragment, factory).get(PostListViewModelFlow::class.java)

    @Provides
    fun providePostListViewModelRxJava3(
        fragment: Fragment,
        factory: PostListRxJava3ViewModelFactory
    ) =
        ViewModelProvider(fragment, factory).get(PostListViewModelRxJava3::class.java)

    @Provides
    fun providePostListViewModel(fragment: Fragment, factory: PostStatusViewModelFactory) =
        ViewModelProvider(fragment, factory).get(PostStatusViewModel::class.java)

    @Provides
    fun provideCoroutineScope() = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
}
