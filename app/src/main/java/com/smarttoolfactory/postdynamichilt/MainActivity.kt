package com.smarttoolfactory.postdynamichilt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var getPostListUseCaseFlow: GetPostListUseCaseFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
