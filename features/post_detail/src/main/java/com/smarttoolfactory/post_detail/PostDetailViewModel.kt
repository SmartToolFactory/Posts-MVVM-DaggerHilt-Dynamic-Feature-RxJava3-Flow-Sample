package com.smarttoolfactory.post_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostsWithStatusUseCaseFlow
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn

class PostDetailViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostsWithStatusUseCaseFlow
) : ViewModel() {

    fun updatePostStatus(post: Post) {
        post.displayCount++
        getPostsUseCase.updatePostStatus(post)
            .launchIn(coroutineScope)
    }
}

class PostDetailViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostsWithStatusUseCaseFlow
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != PostDetailViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return PostDetailViewModel(
            coroutineScope,
            getPostsUseCase
        ) as T
    }
}
