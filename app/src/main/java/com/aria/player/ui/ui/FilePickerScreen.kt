package com.aria.player.ui

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun FilePickerScreen(
    onBack: () -> Unit,
    onVideoListReady: (List<VideoItem>) -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    var videoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            videoList = fetchVideoFiles(context)
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            videoList = fetchVideoFiles(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← بازگشت", color = Color(0xFF1DB954), fontSize = 16.sp)
            }

            if (videoList.isNotEmpty()) {
                Button(
                    onClick = { onVideoListReady(videoList) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("پخش همه (حالت ریلز)", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ویدیوهای موجود در حافظه (${videoList.size})",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (!hasPermission) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color(0xFF1DB954),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "برای نمایش فیلم‌ها به مجوز دسترسی نیاز است",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_MEDIA_VIDEO
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        permissionLauncher.launch(permission)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("اعطای دسترسی ویدیوها", color = Color.White)
                }
            }
        } else if (videoList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("هیچ ویدیویی پیدا نشد", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(videoList) { video ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // شروع پخش از ویدیوی انتخابی
                                val startIndex = videoList.indexOf(video)
                                val rearrangedList = videoList.subList(startIndex, videoList.size) + videoList.subList(0, startIndex)
                                onVideoListReady(rearrangedList)
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF282828)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoFile,
                                contentDescription = null,
                                tint = Color(0xFF1DB954),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = video.title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "آماده پخش - ۱ تا ۳ کلمه",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// تابع استخراج واقعی ویدیوهای گوشی از MediaStore
fun fetchVideoFiles(context: Context): List<VideoItem> {
    val videoList = mutableListOf<VideoItem>()
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val projection = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.TITLE,
        MediaStore.Video.Media.DISPLAY_NAME
    )

    val cursor = context.contentResolver.query(
        collection,
        projection,
        null,
        null,
        "${MediaStore.Video.Media.DATE_ADDED} DESC"
    )

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
        val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)

        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val rawTitle = it.getString(titleColumn) ?: it.getString(nameColumn) ?: "ویدیو جدید"
            
            // خلاصه‌سازی اسم فیلم به حداکثر ۳ کلمه
            val words = rawTitle.split(" ", "_", "-").filter { word -> word.isNotBlank() }
            val formattedTitle = if (words.isNotEmpty()) {
                words.take(3).joinToString(" ")
            } else {
                "فیلم سینمایی"
            }

            val contentUri: Uri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                id
            )

            videoList.add(
                VideoItem(
                    id = id.toString(),
                    title = formattedTitle,
                    uri = contentUri,
                    isSaved = false,
                    isLiked = false
                )
            )
        }
    }
    return videoList
}
