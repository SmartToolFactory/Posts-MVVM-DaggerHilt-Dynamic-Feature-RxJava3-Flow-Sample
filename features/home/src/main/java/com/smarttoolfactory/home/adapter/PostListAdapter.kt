package com.smarttoolfactory.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.home.BR
import com.smarttoolfactory.home.R
import kotlinx.android.synthetic.main.row_post.view.*

class PostListAdapter(

    @LayoutRes private val layoutId: Int,
    private val onItemClicked: ((Post) -> Unit)? = null,
    private val onLikeButtonClicked: ((Post) -> Unit)? = null
) :
    ListAdapter<Post, PostListAdapter.CustomViewHolder<Post>>(
        PostDiffCallback()
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder<Post> {

        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(
                LayoutInflater.from(parent.context),
                layoutId,
                parent,
                false
            )

        return CustomViewHolder<Post>(binding)
            .apply {
                onViewHolderCreated(this, binding)
            }
    }

    /**
     * Add click listener here to prevent setting listener after a ViewHolder every time
     * ViewHolder is scrolled and onBindViewHolder is called
     */
    private fun onViewHolderCreated(
        viewHolder: RecyclerView.ViewHolder,
        binding: ViewDataBinding
    ) {

        binding.root.setOnClickListener {
            onItemClicked?.let {
                it((getItem(viewHolder.bindingAdapterPosition)))
            }
        }

        binding.root.ivLike.setOnClickListener { likeButton ->
            onLikeButtonClicked?.let { onLikeButtonClick ->

                getItem(viewHolder.bindingAdapterPosition).apply {
                    // Change like status of Post
                    isFavorite = !isFavorite
                    onLikeButtonClick(this)

                    // Set image source of like button
                    val imageResource = if (isFavorite) {
                        R.drawable.ic_baseline_thumb_up_24
                    } else {
                        R.drawable.ic_outline_thumb_up_24
                    }
                    (likeButton as? ImageButton)?.setImageResource(imageResource)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder<Post>, position: Int) {
        val item = getItem(position)
        holder.bindTo(item)
    }

    class CustomViewHolder<T> constructor(
        private val binding: ViewDataBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(
            item: T
        ) {
            // Bind item to layout to dispatch data to layout
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.id == newItem.id
    }
}
