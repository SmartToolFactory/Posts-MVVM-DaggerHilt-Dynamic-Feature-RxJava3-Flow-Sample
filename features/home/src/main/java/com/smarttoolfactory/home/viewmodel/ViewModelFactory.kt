package com.smarttoolfactory.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseRxJava3
import com.smarttoolfactory.domain.usecase.GetPostsWithStatusUseCaseFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

// TODO Add SavedStateHandle to ViewModelFactory

class ViewModelFactory

class PostListFlowViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostListUseCaseFlow
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass != PostListViewModelFlow::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }

        return PostListViewModelFlow(
            coroutineScope,
            getPostsUseCase
        ) as T
    }
}

class PostListRxJava3ViewModelFactory @Inject constructor(
    private val getPostsUseCase: GetPostListUseCaseRxJava3
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass != PostListViewModelRxJava3::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }

        return PostListViewModelRxJava3(getPostsUseCase) as T
    }
}

class PostStatusViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostsWithStatusUseCaseFlow
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass != PostStatusViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }

        return PostStatusViewModel(
            coroutineScope,
            getPostsUseCase
        ) as T
    }
}
