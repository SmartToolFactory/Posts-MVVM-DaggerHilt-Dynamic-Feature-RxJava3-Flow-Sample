package com.smarttoolfactory.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.smarttoolfactory.core.util.Event
import com.smarttoolfactory.core.viewstate.ViewState
import com.smarttoolfactory.domain.model.Post

abstract class AbstractPostListVM : ViewModel() {

    companion object {
        const val POST_LIST = "POST_LIST"
        const val POST_DETAIL = "POST_DETAIL"
    }

    abstract val goToDetailScreen: LiveData<Event<Post>>

    abstract val postViewState: LiveData<ViewState<List<Post>>>

    abstract fun getPosts()

    abstract fun refreshPosts()

    abstract fun onClick(post: Post)
}
