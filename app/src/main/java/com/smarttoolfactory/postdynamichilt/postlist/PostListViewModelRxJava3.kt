package com.smarttoolfactory.postdynamichilt.postlist

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.smarttoolfactory.core.util.Event
import com.smarttoolfactory.core.util.convertFromSingleToObservableViewState
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseRxJava3
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class PostListViewModelRxJava3 @ViewModelInject constructor(
    private val getPostsUseCase: GetPostListUseCaseRxJava3,
    @Assisted private val savedStateHandle: SavedStateHandle
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
        getPostsUseCase.getPostsOfflineFirst()
            .convertFromSingleToObservableViewState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _postViewState.value = it
                },
                {
                    _postViewState.value = ViewState(status = Status.ERROR, error = it)
                }
            )
    }

    override fun refreshPosts() {
        getPostsUseCase.getPostsOfflineLast()
            .convertFromSingleToObservableViewState()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _postViewState.value = it
                },
                {
                    _postViewState.value = ViewState(status = Status.ERROR, error = it)
                }
            )
    }

    override fun onClick(post: Post) {
        _goToDetailScreen.value = Event(post)
    }
}
