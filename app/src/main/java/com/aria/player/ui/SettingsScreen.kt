package com.aria.player.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    val context = LocalContext.current
    val primaryColor = AppThemeManager.primaryColor

    // مجوز میکروفون
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            AppVoiceManager.isVoiceControlEnabled = true
        } else {
            AppVoiceManager.isVoiceControlEnabled = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        // نوار بالایی
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("← بازگشت به خانه", color = primaryColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text("تنظیمات پیشرفته", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // قسمت اول: تم رنگی برنامه
            item {
                ExpandableSettingCard(title = "قسمت ۱: تم و رنگ‌بندی سفارشی برنامه") {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("انتخاب رنگ اصلی بخش‌ها (به جز صفحه اصلی):", color = Color.LightGray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        val colors = listOf(
                            Color(0xFF1DB954), // سبز
                            Color(0xFFE91E63), // صورتی/قرمز
                            Color(0xFF2196F3), // آبی
                            Color(0xFF9C27B0), // بنفش
                            Color(0xFFFF9800), // نارنجی
                            Color(0xFF00BCD4)  // فیروزه‌ای
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            colors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (primaryColor == color) 3.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .clickable { AppThemeManager.primaryColor = color }
                                )
                            }
                        }
                    }
                }
            }

            // قسمت دوم: تنظیمات صوت و کیفیت
            item {
                ExpandableSettingCard(title = "قسمت ۲: تنظیمات تخصصی صوت و کیفیت") {
                    var selectedQuality by remember { mutableStateOf(AudioQuality.HIGH) }
                    var surround3D by remember { mutableStateOf(true) }

                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("کیفیت خروجی صدا:", color = Color.LightGray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AudioQuality.values().forEach { quality ->
                                FilterChip(
                                    selected = selectedQuality == quality,
                                    onClick = { selectedQuality = quality },
                                    label = { Text(quality.name, fontSize = 11.sp) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("حالت صدای سه‌بعدی (3D Surround):", color = Color.White, fontSize = 13.sp)
                            Switch(
                                checked = surround3D,
                                onCheckedChange = { surround3D = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = primaryColor)
                            )
                        }
                    }
                }
            }

            // قسمت سوم: حالت صوتی و فرامين گفتاری (میکروفون)
            item {
                ExpandableSettingCard(title = "قسمت ۳: حالت صوتی و کنترل با گفتار (میکروفون)") {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("فعال‌سازی کنترل صوتی (بالا / پایین):", color = Color.White, fontSize = 13.sp)
                            Switch(
                                checked = AppVoiceManager.isVoiceControlEnabled,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        AppVoiceManager.isVoiceControlEnabled = false
                                    }
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = primaryColor)
                            )
                        }

                        if (AppVoiceManager.isVoiceControlEnabled) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("میزان حساسیت میکروفون:", color = Color.LightGray, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                VoiceSensitivity.values().forEach { sens ->
                                    val title = when(sens) {
                                        VoiceSensitivity.LOW -> "کم"
                                        VoiceSensitivity.MEDIUM -> "متوسط"
                                        VoiceSensitivity.ULTRA_HIGH -> "فوق حساس"
                                    }
                                    FilterChip(
                                        selected = AppVoiceManager.sensitivity == sens,
                                        onClick = { AppVoiceManager.sensitivity = sens },
                                        label = { Text(title, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // قسمت چهارم: پوشه ویدیوهای ذخیره‌شده
            item {
                ExpandableSettingCard(title = "قسمت ۴: پوشه ویدیوهای سیو شده (${savedVideos.size})") {
                    VideoListSection(savedVideos, onTitleUpdated, onPlayVideos)
                }
            }

            // قسمت پنجم: پوشه ویدیوهای لایک‌شده
            item {
                ExpandableSettingCard(title = "قسمت ۵: پوشه ویدیوهای لایک شده (${likedVideos.size})") {
                    VideoListSection(likedVideos, onTitleUpdated, onPlayVideos)
                }
            }

            // قسمت ششم: تنظیمات نمایش و ژست‌های حرکتی
            item {
                ExpandableSettingCard(title = "قسمت ۶: تنظیمات ژست‌های لمسی و پخش") {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("• پخش خودکار ویدیوها هنگام جابه‌جایی: فعال", color = Color.LightGray, fontSize = 12.sp)
                        Text("• دوبار ضربه روی صفحه برای لایک: فعال", color = Color.LightGray, fontSize = 12.sp)
                        Text("• کلیک روی آیکون سیو جهت سبز شدن و افزودن به آرشیو: فعال", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }

            // قسمت هفتم: اطلاعات نرم‌افزار و پشتیبانی
            item {
                ExpandableSettingCard(title = "قسمت ۷: درباره نرم‌افزار و گیت‌هاب اکشن") {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("نسخه: 1.0.0 (سازگار با Android & Compose)", color = Color.White, fontSize = 12.sp)
                        Text("پشتیبانی کامپایل: GitHub Actions CI/CD Pipeline", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// کامپوننت آکاردئونی با آیکون + و -
@Composable
fun ExpandableSettingCard(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = null,
                    tint = AppThemeManager.primaryColor
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Box(modifier = Modifier.background(Color(0xFF252525))) {
                    content()
                }
            }
        }
    }
}

@Composable
fun VideoListSection(
    videos: List<VideoItem>,
    onTitleUpdated: (VideoItem, String) -> Unit,
    onPlayVideos: (List<VideoItem>) -> Unit
) {
    if (videos.isEmpty()) {
        Text("ویدیویی وجود ندارد.", color = Color.Gray, modifier = Modifier.padding(12.dp), fontSize = 12.sp)
    } else {
        Column(modifier = Modifier.padding(12.dp)) {
            Button(
                onClick = { onPlayVideos(videos) },
                colors = ButtonDefaults.buttonColors(containerColor = AppThemeManager.primaryColor),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text("پخش این لیست", fontSize = 12.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            videos.forEach { video ->
                Text("• ${video.title}", color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}
