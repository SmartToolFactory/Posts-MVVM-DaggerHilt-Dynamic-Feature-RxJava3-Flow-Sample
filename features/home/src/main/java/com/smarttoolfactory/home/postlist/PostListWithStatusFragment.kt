package com.smarttoolfactory.home.postlist

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttoolfactory.core.di.CoreModuleDependencies
import com.smarttoolfactory.core.ui.fragment.DynamicNavigationFragment
import com.smarttoolfactory.home.R
import com.smarttoolfactory.home.adapter.PostListAdapter
import com.smarttoolfactory.home.databinding.FragmentPostListBinding
import com.smarttoolfactory.home.di.DaggerPostListComponent
import com.smarttoolfactory.home.viewmodel.PostStatusViewModel
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class PostListWithStatusFragment : DynamicNavigationFragment<FragmentPostListBinding>() {

    @Inject
    lateinit var viewModel: PostStatusViewModel

    override fun getLayoutRes(): Int = R.layout.fragment_post_list

    private lateinit var postListAdapter: PostListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        initCoreDependentInjection()
        super.onCreate(savedInstanceState)
        viewModel.getPosts()
    }

    override fun bindViews() {
        dataBinding.viewModel = viewModel

        dataBinding.recyclerView.apply {

            // Set Layout manager
            this.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

            postListAdapter = PostListAdapter(
                R.layout.row_post,
                viewModel::onClick,
                viewModel::onLikeButtonClick
            )

            // Set RecyclerViewAdapter
            this.adapter = postListAdapter
        }

        val swipeRefreshLayout = dataBinding.swipeRefreshLayout

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            viewModel.refreshPosts()
        }

        subscribeGoToDetailScreen()
    }

    private fun subscribeGoToDetailScreen() {

        viewModel.goToDetailScreen.observe(
            viewLifecycleOwner,
            {

                it.getContentIfNotHandled()?.let { post ->
                    val bundle = bundleOf("post" to post)

                    /*
                        Directly calling R.id.nav_graph_post_detail causes compiler to fail
                        with Unresolved reference: nav_graph_post_detail
                     */
                    findNavController().navigate(
                        R.id.action_postListFragment_to_nav_graph_post_detail,
                        bundle
                    )
                }
            }
        )
    }

    private fun initCoreDependentInjection() {

        val coreModuleDependencies = EntryPointAccessors.fromApplication(
            requireActivity().applicationContext,
            CoreModuleDependencies::class.java
        )

        DaggerPostListComponent.factory().create(
            dependentModule = coreModuleDependencies,
            fragment = this
        )
            .inject(this)
    }
}
