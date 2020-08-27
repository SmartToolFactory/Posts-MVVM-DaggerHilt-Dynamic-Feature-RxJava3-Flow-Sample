package com.smarttoolfactory.postdynamichilt

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
import com.smarttoolfactory.postdynamichilt.postlist.PostListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: PostListViewModel by viewModels()

    @Inject
    lateinit var getPostListUseCaseFlow: GetPostListUseCaseFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.getPosts()
    }
}
