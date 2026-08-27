package com.aria.player.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelsPlayerScreen(
    videoList: List<VideoItem>,
    onOpenSettings: () -> Unit,
    onToggleSave: (VideoItem) -> Unit,
    onToggleLike: (VideoItem) -> Unit
) {
    if (videoList.isEmpty()) return

    // ۱. درخواست مجوز دسترسی به ویدیوهای حافظه گوشی
    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(storagePermission)
    }

    // ۲. کنترل اسکرول تیک‌تاکی (محدود به رد شدن دقیقاً ۱ فیلم در هر با کشیدن دست)
    val pagerState = rememberPagerState(pageCount = { videoList.size })
    val coroutineScope = rememberCoroutineScope()

    // تایمر پخش خودکار (در صورت فعال بودن در تنظیمات)
    LaunchedEffect(pagerState.currentPage, AutoPlayManager.isAutoPlayEnabled) {
        if (AutoPlayManager.isAutoPlayEnabled) {
            delay(AutoPlayManager.durationPerVideoSeconds * 1000L)
            if (pagerState.currentPage < videoList.size - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
        }
    }

    // لیسنر صوتی (کنترل با گفتار بالا/پایین)
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
            modifier = Modifier.fillMaxSize(),
            // محدود کردن رد شدن فیلم‌ها فقط به ۱ عدد حتی با دست زدن محکم
            pagerSnapDistance = PagerSnapDistance.atMost(1)
        ) { page ->
            val video = videoList[page]

            var isPaused by remember { mutableStateOf(false) }
            var showLikeHeart by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            // تک‌ضربه: استپ / پخش
                            onTap = { isPaused = !isPaused },
                            // لمس طولانی (۱ تا ۳ ثانیه): استپ هنگام نگه داشتن دست
                            onPress = {
                                tryAwaitRelease()
                                isPaused = false
                            },
                            // دبل تپ: ثبت لایک و افزودن به پوشه لایک شده‌ها
                            onDoubleTap = {
                                if (!video.isLiked) {
                                    onToggleLike(video)
                                    showLikeHeart = true
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                // نمایش متن وضعیت پخش ویدیو
                Text(
                    text = if (isPaused) "⏸ متوقف شد" else "▶ در حال پخش ویدیو",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // انیمیشن قلب قرمز هنگام دبل تپ
                AnimatedVisibility(
                    visible = showLikeHeart,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like Animation",
                        tint = Color.Red,
                        modifier = Modifier.size(100.dp)
                    )
                    LaunchedEffect(Unit) {
                        delay(800)
                        showLikeHeart = false
                    }
                }

                // بخش پایین سمت راست: آیکون سینمایی، نام فیلم چسبیده و آیکون سیو بالایی
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 40.dp, end = 20.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // آیکون سیو (بالای آیکون فیلم سینمایی)
                    IconButton(
                        onClick = { onToggleSave(video) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "ذخیره فیلم",
                            // تغییر رنگ از سبز (سیو شده) به عادی و بالعکس
                            tint = if (video.isSaved) Color(0xFF00FF00) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // ترکیب آیکون سینمایی ثابت و نام فیلم چسبیده به آن در سمت چپ
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        // اسم فیلم در سمت چپ (بزرگ، بولد و صاف)
                        Text(
                            text = video.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        // آیکون سینمایی ثابت (در پایین سمت راست)
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Movie Icon",
                            tint = AppThemeManager.primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // آیکون دکمه تنظیمات لوکس در بالای صفحه
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "تنظیمات",
                        tint = Color.White
                    )
                }
            }
        }

        // دایره سبز چشمک‌زن حالت صوتی در پایین صفحه
        GlowingVoiceIndicator()
    }
}
