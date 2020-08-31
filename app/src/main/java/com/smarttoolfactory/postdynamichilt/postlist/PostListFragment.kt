package com.smarttoolfactory.postdynamichilt.postlist

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smarttoolfactory.core.ui.base.DynamicNavigationFragment
import com.smarttoolfactory.postdynamichilt.R
import com.smarttoolfactory.postdynamichilt.databinding.FragmentPostListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostListFragment : DynamicNavigationFragment<FragmentPostListBinding>() {

    override fun getLayoutRes(): Int = R.layout.fragment_post_list

    //    private val viewModel: PostListViewModel by viewModels()
    private val viewModel: PostStatusViewModel by viewModels()

    private lateinit var postListAdapter: PostListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

                    findNavController().navigate(
                        R.id.nav_graph_post_detail,
                        bundle
                    )
                }
            }
        )
    }
}
