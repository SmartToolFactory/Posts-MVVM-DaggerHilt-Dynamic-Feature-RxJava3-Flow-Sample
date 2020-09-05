package com.smarttoolfactory.home.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttoolfactory.core.util.Event
import com.smarttoolfactory.core.util.convertToFlowViewState
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class PostListViewModelFlow @ViewModelInject constructor(
    private val coroutineScope: CoroutineScope,
    private val getPostsUseCase: GetPostListUseCaseFlow
//    @Assisted savedStateHandle: SavedStateHandle
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

    /**
     * Function to retrieve data from repository with offline-first which checks
     * local data source first.
     *
     * * Check out Local Source first
     * * If empty data or null returned throw empty set exception
     * * If error occurred while fetching data from remote: Try to fetch data from db
     * * If data is fetched from remote source: delete old data, save new data and return new data
     * * If both network and db don't have any data throw empty set exception
     *
     */
    override fun getPosts() {

        getPostsUseCase.getPostFlowOfflineFirst()
            .convertToFlowViewState()
            .onStart {
                _postViewState.value = ViewState(status = Status.LOADING)
                /*
                    🔥 This is artificial delay since db fetches data quickly and looks like a lag
                    when navigating from previous ViewPager2 page
                 */
                delay(250)
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
