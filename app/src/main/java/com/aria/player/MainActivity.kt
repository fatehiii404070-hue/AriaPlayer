package com.aria.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aria.player.ui.FilePickerScreen
import com.aria.player.ui.PlayerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("home") }
            var selectedSongTitle by remember { mutableStateOf("عنوان موزیک (در حال پخش)") }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF121212)
            ) {
                when (currentScreen) {
                    "home" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(Color(0xFF1DB954), shape = RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = "Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "آریا پلیر (AriaPlayer)",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "نسخه هوشمند - پخش حرفه‌ای فایل‌های صوتی",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(48.dp))

                            Button(
                                onClick = { currentScreen = "file_picker" },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282828)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Folder, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("انتخاب از حافظه گوشی", color = Color.White, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { currentScreen = "player" },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("پخش سریع", color = Color.White, fontSize = 16.sp)
                            }
                        }
                    }
                    "file_picker" -> {
                        FilePickerScreen(
                            onBack = { currentScreen = "home" },
                            onSongSelected = { songName ->
                                selectedSongTitle = songName
                                currentScreen = "player"
                            }
                        )
                    }
                    "player" -> {
                        PlayerScreen(
                            songTitle = selectedSongTitle,
                            onBack = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}
