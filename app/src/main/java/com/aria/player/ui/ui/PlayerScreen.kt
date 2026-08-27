package com.aria.player.ui

import android.net.Uri
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun PlayerScreen(
    videoUri: Uri?,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // ساخت موتور پخش ویدیو ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            videoUri?.let {
                setMediaItem(MediaItem.fromUri(it))
                prepare()
                playWhenReady = true // پخش خودکار فیلم (Autoplay)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // کامپوننت نمایش فیلم با قابلیت Touch and Hold برای توقف/ادامه
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true // نمایش کنترل‌های پیش‌فرض فیلم
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // لمس و نگه داشتن انگشت -> متوقف شدن فیلم
                            exoPlayer.pause()
                            true
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            // برداشتن انگشت از روی صفحه -> ادامه پخش فیلم
                            exoPlayer.play()
                            true
                        }
                        else -> false
                    }
                }
        )

        // دکمه بازگشت بالای صفحه فیلم
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x88000000)),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text("← بازگشت", color = Color.White, fontSize = 14.sp)
        }
    }
}
