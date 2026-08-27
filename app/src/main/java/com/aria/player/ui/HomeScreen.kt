package com.aria.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onStartPlay: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // لوگوی سینمایی سه‌بعدی
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(30.dp), spotColor = Color(0xFF1DB954))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF25D366), Color(0xFF0E642A))
                        ),
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = "لوگو فیلم",
                    tint = Color.White,
                    modifier = Modifier.size(75.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "آریا پلیر",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // دکمه بزرگ پخش فیلم
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                    )
                )
                .clickable { onStartPlay() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF121212),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "پخش فیلم",
                    color = Color(0xFF121212),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // دکمه کوچکتر تنظیمات
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(65.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFE066), Color(0xFFFFB703))
                    )
                )
                .clickable { onOpenSettings() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF121212),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تنظیمات",
                    color = Color(0xFF121212),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

