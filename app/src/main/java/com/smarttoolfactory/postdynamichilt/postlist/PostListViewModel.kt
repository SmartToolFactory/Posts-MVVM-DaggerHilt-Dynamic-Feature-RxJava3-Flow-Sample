package com.smarttoolfactory.postdynamichilt.postlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.smarttoolfactory.core.util.Event
import com.smarttoolfactory.core.util.convertToFlowViewState
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class PostListViewModel @ViewModelInject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostListUseCaseFlow,
    @Assisted savedStateHandle: SavedStateHandle
) : AbstractPostListVM() {

    private val _goToDetailScreen =
        savedStateHandle.getLiveData<Event<Post>>(POST_DETAIL)

    override val goToDetailScreen: LiveData<Event<Post>>
        get() = _goToDetailScreen

    private val _postViewState =
        savedStateHandle.getLiveData<ViewState<List<Post>>>(POST_LIST)

    override val postViewState: LiveData<ViewState<List<Post>>>
        get() = _postViewState

    override fun getPosts() {

        getPostsUseCase.getPostFlowOfflineFirst()
            .convertToFlowViewState()
            .onStart {
                _postViewState.value = ViewState(status = Status.LOADING)
            }
            .onEach {
                _postViewState.value = it
            }
            .launchIn(coroutineScope)
    }

    override fun refreshPosts() {
        getPostsUseCase.getPostFlowOfflineLast()
            .convertToFlowViewState()
            .onStart {
                _postViewState.value = ViewState(status = Status.LOADING)
            }
            .onEach {
                _postViewState.value = it
            }
            .launchIn(coroutineScope)
    }

    override fun onClick(post: Post) {
        _goToDetailScreen.value = Event(post)
    }
}
