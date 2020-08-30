package com.smarttoolfactory.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String,
    var displayCount: Int = 0,
    var isFavorite: Boolean = false
) : Parcelable
