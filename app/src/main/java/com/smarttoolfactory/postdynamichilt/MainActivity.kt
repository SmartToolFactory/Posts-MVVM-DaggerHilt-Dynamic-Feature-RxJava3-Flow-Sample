package com.smarttoolfactory.postdynamichilt

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
import com.smarttoolfactory.postdynamichilt.postlist.PostListViewModelRxJava3
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //    private val viewModel: PostListViewModel by viewModels()
    private val viewModel: PostListViewModelRxJava3 by viewModels()

    @Inject
    lateinit var getPostListUseCaseFlow: GetPostListUseCaseFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.getPosts()

        viewModel.postViewState.observe(
            this,
            Observer {
                println("🔥 MainActivity view state: ${it.status}")
            }
        )
    }
}
