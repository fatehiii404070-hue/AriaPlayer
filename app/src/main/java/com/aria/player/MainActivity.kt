package com.aria.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.aria.player.ui.*

enum class Screen {
    HOME,
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
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var currentVideoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                onStartPlay = {
                    currentScreen = Screen.FILE_PICKER
                },
                onOpenSettings = {
                    currentScreen = Screen.SETTINGS
                }
            )
        }

        Screen.FILE_PICKER -> {
            FilePickerScreen(
                onBack = { currentScreen = Screen.HOME },
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
            val likedVideos = currentVideoList.filter { it.isLiked }

            SettingsScreen(
                savedVideos = savedVideos,
                likedVideos = likedVideos,
                onBack = { currentScreen = Screen.HOME },
                onTitleUpdated = { targetVideo, newTitle ->
                    currentVideoList = currentVideoList.map { item ->
                        if (item.id == targetVideo.id) item.copy(title = newTitle) else item
                    }
                },
                onPlayVideos = { selectedList ->
                    currentVideoList = selectedList
                    currentScreen = Screen.REELS_PLAYER
                }
            )
        }
    }
}
