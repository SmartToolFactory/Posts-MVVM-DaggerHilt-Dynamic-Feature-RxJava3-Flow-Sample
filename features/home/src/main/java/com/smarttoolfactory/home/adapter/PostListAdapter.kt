package com.smarttoolfactory.home.adapter

import android.widget.ImageButton
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.smarttoolfactory.core.ui.adapter.BaseListAdapter
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.home.BR
import com.smarttoolfactory.home.R
import com.smarttoolfactory.home.databinding.RowPostBinding

class PostListAdapter(
    @LayoutRes private val layoutId: Int,
    private val onItemClicked: ((Post) -> Unit)? = null,
    private val onLikeButtonClicked: ((Post) -> Unit)? = null
) :
    BaseListAdapter<Post>(
        layoutId,
        PostDiffCallback()
    ) {

    override fun onViewHolderBound(
        binding: ViewDataBinding,
        item: Post,
        position: Int,
        payloads: MutableList<Any>
    ) {
        binding.setVariable(BR.item, item)
    }

    /**
     * Add click listener here to prevent setting listener after a ViewHolder every time
     * ViewHolder is scrolled and onBindViewHolder is called
     */
    override fun onViewHolderCreated(
        viewHolder: RecyclerView.ViewHolder,
        viewType: Int,
        binding: ViewDataBinding
    ) {

        binding.root.setOnClickListener {
            onItemClicked?.let {
                it((getItem(viewHolder.bindingAdapterPosition)))
            }
        }

        if (binding is RowPostBinding) {

            binding.ivLike.setOnClickListener { likeButton ->
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
