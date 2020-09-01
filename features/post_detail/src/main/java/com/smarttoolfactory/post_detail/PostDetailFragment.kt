package com.smarttoolfactory.post_detail

import android.os.Bundle
import com.smarttoolfactory.core.di.CoreModuleDependencies
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.post_detail.databinding.FragmentPostDetailBinding
import com.smarttoolfactory.post_detail.di.DaggerPostDetailComponent
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class PostDetailFragment : DynamicNavigationFragment<FragmentPostDetailBinding>() {

    @Inject
    lateinit var viewModel: PostDetailViewModel

    override fun getLayoutRes(): Int = R.layout.fragment_post_detail

    override fun bindViews() {
        // Get Post from navigation component arguments
        val post = arguments?.get("post") as Post
        dataBinding.item = post
        viewModel.updatePostStatus(post)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initCoreDependentInjection()
        super.onCreate(savedInstanceState)
    }

    private fun initCoreDependentInjection() {

        val coreModuleDependencies = EntryPointAccessors.fromApplication(
            requireActivity().applicationContext,
            CoreModuleDependencies::class.java
        )

        DaggerPostDetailComponent.factory().create(
            dependentModule = coreModuleDependencies,
            fragment = this
        )
            .inject(this)
    }
}
