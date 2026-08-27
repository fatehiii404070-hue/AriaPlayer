package com.aria.player.ui

import android.net.Uri

data class VideoItem(
    val id: String,
    val title: String,
    val uri: Uri,
    val isSaved: Boolean = false,
    val isLiked: Boolean = false
)

