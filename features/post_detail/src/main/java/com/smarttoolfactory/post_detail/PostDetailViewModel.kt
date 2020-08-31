package com.smarttoolfactory.post_detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostsWithStatusUseCaseFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class PostDetailViewModel @ViewModelInject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostsWithStatusUseCaseFlow
) : ViewModel() {

    fun updatePostStatus(post: Post) {

        post.displayCount++

        getPostsUseCase.updatePostStatus(post)
            .onStart { println("⏰ PostStatusViewModel updatePostStatus() catch() onStart") }
            .catch { throwable ->
                println("❌ PostStatusViewModel updatePostStatus() catch(): ${throwable.message}")
            }
            .onCompletion { cause: Throwable? ->
                println(
                    "💀 PostStatusViewModel updatePostStatus() onCompletion()" +
                        " error: ${cause != null}"
                )
            }
            .launchIn(coroutineScope)
    }
}
