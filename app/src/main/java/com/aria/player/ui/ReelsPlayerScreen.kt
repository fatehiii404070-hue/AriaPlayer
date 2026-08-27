package com.aria.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
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

    // تایمر خودکار برای پخش فیلم بر اساس ثانیه‌های تعبیه‌شده در تنظیمات
    LaunchedEffect(pagerState.currentPage, AutoPlayManager.isAutoPlayEnabled) {
        if (AutoPlayManager.isAutoPlayEnabled) {
            delay(AutoPlayManager.durationPerVideoSeconds * 1000L)
            if (pagerState.currentPage < videoList.size - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    // لیسنر کنترل صوتی (میکروفون)
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
                // کادر لوکس پخش فیلم
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.92f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF111111)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "در حال پخش: ${video.title}",
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    // آیکون لوکس تنظیمات سفارشی‌شده در پایین کادر پخش فیلم
                    Surface(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تنظیمات",
                                tint = AppThemeManager.primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLanguageManager.getTranslation("تنظیمات", "Settings", "الإعدادات", "設定", "设置", "सेटिंग्स"),
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // دکمه‌های اکشن در سمت راست (لایک و سیو)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(20.dp),
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

        // دایره چشمک‌زن سبز کنترل صوتی
        GlowingVoiceIndicator()
    }
}
