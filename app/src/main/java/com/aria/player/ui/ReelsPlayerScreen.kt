package com.aria.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ReelsPlayerScreen(
    videoList: List<VideoItem>,
    onOpenSettings: () -> Unit,
    onToggleSave: (VideoItem) -> Unit,
    onToggleLike: (VideoItem) -> Unit
) {
    if (videoList.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { videoList.size })
    val coroutineScope = rememberCoroutineScope()

    // اتصال کنترل صوتی جهت بالا و پایین کردن ویدیوها
    VoiceControlListener(
        onSwipeUp = {
            if (pagerState.currentPage < videoList.size - 1) {
                coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            }
        },
        onSwipeDown = {
            if (pagerState.currentPage > 0) {
                coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val video = videoList[page]

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // نمایش عنوان ویدیو در صفحه پخش
                Text(
                    text = "در حال پخش: ${video.title}",
                    color = Color.White
                )

                // دکمه‌های لایک و سیو در سمت راست
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(onClick = { onToggleLike(video) }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "لایک",
                            tint = if (video.isLiked) Color.Red else Color.White
                        )
                    }

                    IconButton(onClick = { onToggleSave(video) }) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "سیو",
                            tint = if (video.isSaved) AppThemeManager.primaryColor else Color.White
                        )
                    }
                }
            }
        }

        // آیکون تنظیمات در بالای صفحه
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = "تنظیمات", tint = Color.White)
        }

        // نقطه سبز رنگ چشمک‌زن صوتی در پایین صفحه
        GlowingVoiceIndicator()
    }
}

