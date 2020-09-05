package com.smarttoolfactory.home.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttoolfactory.core.util.Event
import com.smarttoolfactory.core.util.convertFromSingleToObservableViewState
import com.smarttoolfactory.core.util.convertFromSingleToObservableViewStateWithLoading
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseRxJava3
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class PostListViewModelRxJava3 @ViewModelInject constructor(
    private val getPostsUseCase: GetPostListUseCaseRxJava3
//    @Assisted private val savedStateHandle: SavedStateHandle
) : AbstractPostListVM() {

    // TODO Add SavedStateHandle to ViewModelFactory and uncomment
//    private val _goToDetailScreen =
//        savedStateHandle.getLiveData<Event<Post>>(POST_DETAIL)

    private val _goToDetailScreen = MutableLiveData<Event<Post>>()

    override val goToDetailScreen: LiveData<Event<Post>>
        get() = _goToDetailScreen

//    private val _postViewState =
//        savedStateHandle.getLiveData<ViewState<List<Post>>>(POST_LIST)

    private val _postViewState = MutableLiveData<ViewState<List<Post>>>()

    override val postViewState: LiveData<ViewState<List<Post>>>
        get() = _postViewState

    override fun getPosts() {
        getPostsUseCase.getPostsOfflineFirst()
            /*
                🔥 This is artificial delay since db fetches data quickly and looks like a lag
                when navigating from previous ViewPager2 page
             */.delay(250, TimeUnit.MILLISECONDS)
            .convertFromSingleToObservableViewStateWithLoading()
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
