package com.aria.player.service

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class AudioPlayerManager(context: Context) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()

    fun playAudio(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun pauseAudio() {
        player.pause()
    }

    fun resumeAudio() {
        player.play()
    }

    fun release() {
        player.release()
    }
}

