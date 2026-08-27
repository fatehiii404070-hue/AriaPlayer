package com.aria.player.ui

import android.net.Uri
import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class VideoItem(
    val id: String,
    val title: String, // عنوان فیلم (۱ تا ۳ کلمه)
    val uri: Uri,
    var isSaved: Boolean = false,
    var isLiked: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoFeedScreen(
    videoList: List<VideoItem>,
    onBack: () -> Unit,
    onSaveToggle: (VideoItem) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { videoList.size })
    // رفتار اسکرول اختصاصی: فقط یک ویدیو با هر قدرت دست رد می‌شود
    val snapFlingBehavior = rememberSnapFlingBehavior(pagerState = pagerState)

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        VerticalPager(
            state = pagerState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            SingleVideoPlayer(
                videoItem = videoList[page],
                isCurrentPage = (pagerState.currentPage == page),
                onSaveToggle = onSaveToggle
            )
        }

        // دکمه بازگشت
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 40.dp, start = 16.dp).align(Alignment.TopStart)
        ) {
            Text("←", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SingleVideoPlayer(
    videoItem: VideoItem,
    isCurrentPage: Boolean,
    onSaveToggle: (VideoItem) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLiked by remember { mutableStateOf(videoItem.isLiked) }
    var isSaved by remember { mutableStateOf(videoItem.isSaved) }
    var showLikeHeart by remember { mutableStateOf(false) }

    var lastTapTime by remember { mutableLongStateOf(0L) }
    var touchHoldJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoItem.uri))
            prepare()
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
        }
    }

    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ویدیو پلیر با لمس هوشمند
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    val currentTime = System.currentTimeMillis()

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // لمس و نگه داشتن ۱ الی ۳ ثانیه
                            touchHoldJob = coroutineScope.launch {
                                delay(300)
                                exoPlayer.pause()
                            }

                            // بررسی دابل‌تپ (لایک)
                            if (currentTime - lastTapTime < 300) {
                                touchHoldJob?.cancel()
                                if (!isLiked) { // فقط یک بار قابلیت لایک در طول پخش
                                    isLiked = true
                                    videoItem.isLiked = true
                                    showLikeHeart = true
                                    coroutineScope.launch {
                                        delay(800)
                                        showLikeHeart = false
                                    }
                                }
                            }
                            lastTapTime = currentTime
                            true
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            touchHoldJob?.cancel()
                            if (!exoPlayer.isPlaying) {
                                exoPlayer.play()
                            } else if (currentTime - lastTapTime >= 300) {
                                // تک‌ضرب ساده برای پاز / پلی
                                if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                            }
                            true
                        }
                        else -> false
                    }
                }
        )

        // انیمیشن قلبی شکل لایک وسط صفحه
        AnimatedVisibility(
            visible = showLikeHeart,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(110.dp)
            )
        }

        // عناصر UI روی ویدیو (پایین سمت راست و چپ)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            // ۱. آیکون سیو (بالای آیکون فیلم)
            IconButton(
                onClick = {
                    isSaved = !isSaved
                    videoItem.isSaved = isSaved
                    onSaveToggle(videoItem)
                },
                modifier = Modifier
                    .background(Color(0x66000000), shape = RoundedCornerShape(50))
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Save",
                    tint = if (isSaved) Color(0xFF00FF66) else Color.White, // سبز شدن در صورت سیو
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ۲. ترکیب آیکون فیلم سینمایی و اسم فیلم چسبیده به سمت چپ آن
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // نام فیلم (۱ تا ۳ کلمه - بزرگ، پررنگ و با کیفیت)
                Text(
                    text = videoItem.title.take(25),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0x88000000), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // آیکون لوکس فیلم سینمایی
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF1DB954), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Movie Icon",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
