package com.aria.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(
    savedVideos: List<VideoItem>,
    likedVideos: List<VideoItem>,
    onBack: () -> Unit,
    onTitleUpdated: (VideoItem, String) -> Unit,
    onPlayVideos: (List<VideoItem>) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var editingVideo by remember { mutableStateOf<VideoItem?>(null) }
    var newTitleText by remember { mutableStateOf("") }

    val currentList = if (selectedTab == 0) savedVideos else likedVideos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← بازگشت به خانه", color = Color(0xFF1DB954), fontSize = 16.sp)
            }

            if (currentList.isNotEmpty()) {
                Button(
                    onClick = { onPlayVideos(currentList) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("پخش این لیست", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "تنظیمات و آرشیو",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF1E1E1E),
            contentColor = Color(0xFF1DB954)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("پوشه سیو شده‌ها (${savedVideos.size})", color = if (selectedTab == 0) Color(0xFF1DB954) else Color.Gray) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("لایک شده‌ها (${likedVideos.size})", color = if (selectedTab == 1) Color(0xFF1DB954) else Color.Gray) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Default.Bookmark else Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedTab == 0) "هیچ ویدیویی ذخیره نشده است" else "هیچ ویدیویی لایک نشده است",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList) { video ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF282828)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = video.title,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "عنوان ۱ تا ۳ کلمه‌ای",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }

                            IconButton(
                                onClick = {
                                    editingVideo = video
                                    newTitleText = video.title
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "ویرایش عنوان",
                                    tint = Color(0xFF1DB954)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editingVideo?.let { video ->
        AlertDialog(
            onDismissRequest = { editingVideo = null },
            title = { Text("ویرایش عنوان ویدیو", color = Color.White) },
            text = {
                Column {
                    Text("عنوان جدید (حداکثر ۳ کلمه):", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newTitleText,
                        onValueChange = { input ->
                            val words = input.trim().split("\\s+".toRegex())
                            if (words.size <= 3) newTitleText = input
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1DB954),
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitleText.isNotBlank()) {
                            onTitleUpdated(video, newTitleText.trim())
                        }
                        editingVideo = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Text("ثبت", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingVideo = null }) {
                    Text("انصراف", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF282828)
        )
    }
}

