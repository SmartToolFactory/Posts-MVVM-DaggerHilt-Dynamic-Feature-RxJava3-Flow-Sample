package com.smarttoolfactory.home.util

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.home.R

/*
    *** Bindings for RecyclerView ***
 */

/**
 * [BindingAdapter]s for the [Post]s to ListAdapter.
 */
@BindingAdapter("app:items")
fun RecyclerView.setItems(items: List<Post>?) {

    items?.let {
        (adapter as ListAdapter<Post, *>)?.submitList(items)
    }
}

/**
 * Binding adapter used with this class android:src used with binding of this object
 * loads image from url into specified view
 *
 * @param view image to be loaded into
 * @param path of the image to be fetched
 */
@BindingAdapter("imageSrc")
fun setImageUrl(view: ImageView, userId: Int) {

    try {

        val modulus = 6

        val drawableRes = when {
            userId % modulus == 0 -> {
                R.drawable.avatar_1_raster
            }
            userId % modulus == 1 -> {
                R.drawable.avatar_2_raster
            }
            userId % modulus == 2 -> {
                R.drawable.avatar_3_raster
            }
            userId % modulus == 3 -> {
                R.drawable.avatar_4_raster
            }
            userId % modulus == 4 -> {
                R.drawable.avatar_5_raster
            }
            else -> {
                R.drawable.avatar_6_raster
            }
        }

        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.ic_launcher_background)

        Glide
            .with(view.context)
            .setDefaultRequestOptions(requestOptions)
            .load(drawableRes)
            .into(view)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Display or hide a view based on a condition
 */
@BindingAdapter("visibilityBasedOn")
fun View.visibilityBasedOn(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

@BindingAdapter("favoriteImageSrc")
fun ImageButton.setFavoriteImageSrc(favorite: Boolean) {

    val imageResource = if (favorite) R.drawable.ic_baseline_thumb_up_24
    else R.drawable.ic_outline_thumb_up_24

    setImageResource(imageResource)
}
