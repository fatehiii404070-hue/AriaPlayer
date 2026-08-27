package com.aria.player.ui

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

data class VideoItem(
    val id: String,
    val uri: Uri,
    val title: String
)

@Composable
fun PlayerScreen(videoUri: Uri?) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }

    DisposableEffect(videoUri) {
        val player = ExoPlayer.Builder(context).build().apply {
            videoUri?.let {
                setMediaItem(MediaItem.fromUri(it))
                prepare()
                playWhenReady = true
            }
        }
        exoPlayer = player

        onDispose {
            player.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        exoPlayer?.let { player ->
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
