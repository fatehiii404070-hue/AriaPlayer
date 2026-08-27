package com.aria.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aria.player.ui.FilePickerScreen
import com.aria.player.ui.ReelsPlayerScreen
import com.aria.player.ui.SettingsScreen
import com.aria.player.ui.VideoItem

enum class Screen {
    FILE_PICKER,
    REELS_PLAYER,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(Screen.FILE_PICKER) }
    var currentVideoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }

    when (currentScreen) {
        Screen.FILE_PICKER -> {
            FilePickerScreen(
                onBack = { /* در صفحه اول بازگشت نیازی نیست */ },
                onVideoListReady = { videos ->
                    currentVideoList = videos
                    currentScreen = Screen.REELS_PLAYER
                }
            )
        }

        Screen.REELS_PLAYER -> {
            ReelsPlayerScreen(
                videoList = currentVideoList,
                onOpenSettings = {
                    currentScreen = Screen.SETTINGS
                },
                onToggleSave = { video ->
                    currentVideoList = currentVideoList.map { item ->
                        if (item.id == video.id) item.copy(isSaved = !item.isSaved) else item
                    }
                },
                onToggleLike = { video ->
                    currentVideoList = currentVideoList.map { item ->
                        if (item.id == video.id) item.copy(isLiked = !item.isLiked) else item
                    }
                }
            )
        }

        Screen.SETTINGS -> {
            val savedVideos = currentVideoList.filter { it.isSaved }
            
            SettingsScreen(
                savedVideos = savedVideos,
                onBack = {
                    currentScreen = Screen.REELS_PLAYER
                },
                onTitleUpdated = { targetVideo, newTitle ->
                    currentVideoList = currentVideoList.map { item ->
                        if (item.id == targetVideo.id) item.copy(title = newTitle) else item
                    }
                },
                onPlaySavedVideos = { savedList ->
                    currentVideoList = savedList
                    currentScreen = Screen.REELS_PLAYER
                }
            )
        }
    }
}
