package com.aria.player.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

@Composable
fun SettingsScreen(
    savedVideos: List<VideoItem>,
    likedVideos: List<VideoItem>,
    onBack: () -> Unit,
    onTitleUpdated: (VideoItem, String) -> Unit,
    onPlayVideos: (List<VideoItem>) -> Unit
) {
    val primaryColor = AppThemeManager.primaryColor
    val createdFolders = remember { mutableStateListOf<CustomFolder>() }

    // مجوز میکروفون
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        AppVoiceManager.isVoiceControlEnabled = isGranted
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .padding(16.dp)
    ) {
        // نوار بالایی اپلی (Apple-style Top Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    AppLanguageManager.getTranslation("← بازگشت", "← Back", "← عودة", "← 戻る", "← 返回", "← वापस"),
                    color = primaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                AppLanguageManager.getTranslation("تنظیمات فوق پیشرفته", "Advanced Settings", "الإعدادات المتقدمة", "高度な設定", "高级设置", "उन्नत सेटिंग्स"),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // قسمت ۱: تم و رنگ‌بندی
            item {
                ExpandableSettingCard(
                    title = AppLanguageManager.getTranslation("قسمت ۱: تم و رنگ‌بندی برنامه", "Part 1: Theme & Colors", "الجزء ١: المظهر والألوان", "パート1：テーマと色", "第1部分：主题和颜色", "भाग 1: थीम और रंग")
                ) {
                    ThemeSelectionSection()
                }
            }

            // قسمت ۲: تنظیمات کیفیت صدا
            item {
                ExpandableSettingCard(
                    title = AppLanguageManager.getTranslation("قسمت ۲: تنظیمات تخصصی صوت", "Part 2: Audio Settings", "الجزء ٢: إعدادات الصوت", "パート2：オーディオ設定", "第2部分：音频设置", "भाग 2: ऑडियो सेटिंग्स")
                ) {
                    AudioSettingsSection()
                }
            }

            // قسمت ۳: کنترل صوتی (میکروفون)
            item {
                ExpandableSettingCard(
                    title = AppLanguageManager.getTranslation("قسمت ۳: حالت صوتی و میکروفون", "Part 3: Voice Control", "الجزء ٣: التحكم الصوتي", "パート3：音声コントロール", "第3部分：语音控制", "भाग 3: वॉयस कंट्रोल")
                ) {
                    VoiceControlSection(micPermissionLauncher)
                }
            }

            // قسمت ۴: پوشه‌سازی، آیکون‌ها، ویرایش نام و انتخاب تا ۱۰۰ فیلم
            item {
                ExpandableSettingCard(
                    title = AppLanguageManager.getTranslation("قسمت ۴: مدیریت پوشه‌ها و تغییر نام فیلم‌ها", "Part 4: Folders & Rename", "الجزء ٤: إدارة المجلدات", "パート4：フォルダとリネーム", "第4部分：文件夹与重命名", "भाग 4: फ़ोल्डर और नाम बदलें")
                ) {
                    FolderAndRenameSection(createdFolders)
                }
            }

            // قسمت ۵: حالت پخش خودکار فیلم‌ها
            item {
                ExpandableSettingCard(
                    title = AppLanguageManager.getTranslation("قسمت ۵: حالت خودکار فیلم", "Part 5: Auto Play Mode", "الجزء ٥: التشغيل التلقائي", "パート5：自動再生モード", "第5部分：自动播放模式", "भाग 5: ऑटो प्ले मोड")
                ) {
                    AutoPlaySection()
                }
            }

            // قسمت ۶: تغییر زبان زنده
            item {
                ExpandableSettingCard(
                    title = AppLanguageManager.getTranslation("قسمت ۶: تغییر زبان برنامه", "Part 6: Language Settings", "الجزء ٦: تغيير اللغة", "パート6：言語設定", "第6部分：语言设置", "भाग 6: भाषा सेटिंग्स")
                ) {
                    LanguageSelectionSection()
                }
            }

            // قسمت ۷: اطلاعات و گیت‌هاب اکشن
            item {
                ExpandableSettingCard(
                    title = AppLanguageManager.getTranslation("قسمت ۷: درباره سیستم", "Part 7: System Info", "الجزء ٧: حول النظام", "パート7：システム情報", "第7部分：系统信息", "भाग 7: सिस्टम जानकारी")
                ) {
                    Text(
                        "iOS Glassmorphic Engine v2.0 - Fully Compatible with GitHub Actions CI/CD",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

// بخش انتخاب تم
@Composable
fun ThemeSelectionSection() {
    val primaryColor = AppThemeManager.primaryColor
    val colors = listOf(Color(0xFF1DB954), Color(0xFFE91E63), Color(0xFF2196F3), Color(0xFF9C27B0), Color(0xFFFF9800), Color(0xFF00BCD4))

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(width = if (primaryColor == color) 3.dp else 0.dp, color = Color.White, shape = CircleShape)
                    .clickable { AppThemeManager.primaryColor = color }
            )
        }
    }
}

// بخش تنظیمات صوت
@Composable
fun AudioSettingsSection() {
    var selectedQuality by remember { mutableStateOf(AudioQuality.HIGH) }
    Column(modifier = Modifier.padding(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AudioQuality.values().forEach { q ->
                FilterChip(
                    selected = selectedQuality == q,
                    onClick = { selectedQuality = q },
                    label = { Text(q.name, fontSize = 11.sp) }
                )
            }
        }
    }
}

// بخش کنترل صوتی
@Composable
fun VoiceControlSection(micPermissionLauncher: androidx.activity.result.ActivityResultLauncher<String>) {
    Column(modifier = Modifier.padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("فعال‌سازی کنترل صوتی:", color = Color.White, fontSize = 13.sp)
            Switch(
                checked = AppVoiceManager.isVoiceControlEnabled,
                onCheckedChange = { isChecked ->
                    if (isChecked) micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    else AppVoiceManager.isVoiceControlEnabled = false
                }
            )
        }
    }
}

// بخش ۴: مدیریت کامل پوشه‌ها و انتخاب ۱۰۰ فیلم همزمان
@Composable
fun FolderAndRenameSection(createdFolders: androidx.compose.runtime.snapshots.SnapshotStateList<CustomFolder>) {
    var newFolderName by remember { mutableStateOf("") }
    var selectedIconIndex by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf("") }

    val iconsList = listOf(
        Icons.Default.Folder, Icons.Default.Movie, Icons.Default.Star,
        Icons.Default.Favorite, Icons.Default.VideoLibrary, Icons.Default.MusicNote
    )

    // انتخاب همزمان تا ۱۰۰ فیلم از حافظه گوشی
    val multipleVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 100)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty() && createdFolders.isNotEmpty()) {
            uris.forEach { uri ->
                createdFolders.last().videoUris.add(uri.toString())
            }
        }
    }

    Column(modifier = Modifier.padding(12.dp)) {
        Text("ایجاد پوشه جدید با نام سفارشی (حداکثر ۳ کلمه):", color = Color.LightGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = newFolderName,
            onValueChange = { input ->
                // شرط محدودیت حداکثر ۳ کلمه
                val words = input.trim().split("\\s+".toRegex())
                if (input.isEmpty() || words.size <= 3) {
                    newFolderName = input
                    errorMessage = ""
                } else {
                    errorMessage = "نام پوشه یا فیلم نباید بیشتر از ۳ کلمه باشد!"
                }
            },
            label = { Text("نام پوشه", fontSize = 11.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("انتخاب آیکون پوشه:", color = Color.White, fontSize = 12.sp)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(vertical = 6.dp)) {
            items(iconsList.size) { index ->
                IconButton(
                    onClick = { selectedIconIndex = index },
                    modifier = Modifier.background(
                        if (selectedIconIndex == index) AppThemeManager.primaryColor else Color.DarkGray,
                        shape = CircleShape
                    )
                ) {
                    Icon(iconsList[index], contentDescription = null, tint = Color.White)
                }
            }
        }

        Button(
            onClick = {
                if (newFolderName.isNotBlank() && errorMessage.isEmpty()) {
                    createdFolders.add(
                        CustomFolder(
                            id = UUID.randomUUID().toString(),
                            name = newFolderName,
                            iconIndex = selectedIconIndex
                        )
                    )
                    newFolderName = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppThemeManager.primaryColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ایجاد پوشه جدید", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // نمایش پوشه‌های ساخته‌شده
        createdFolders.forEach { folder ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(iconsList[folder.iconIndex], contentDescription = null, tint = AppThemeManager.primaryColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(folder.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            multipleVideoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Text("افزودن ۱ تا ۱۰۰ فیلم", fontSize = 10.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

// بخش ۵: پخش خودکار پیشرفته
@Composable
fun AutoPlaySection() {
    Column(modifier = Modifier.padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("حالت خودکار فیلم:", color = Color.White, fontSize = 13.sp)
            Switch(
                checked = AutoPlayManager.isAutoPlayEnabled,
                onCheckedChange = { AutoPlayManager.isAutoPlayEnabled = it }
            )
        }

        if (AutoPlayManager.isAutoPlayEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("تعداد فیلم‌ها برای پخش خودکار: ${AutoPlayManager.autoPlayCount}", color = Color.LightGray, fontSize = 12.sp)
            Slider(
                value = AutoPlayManager.autoPlayCount.toFloat(),
                onValueChange = { AutoPlayManager.autoPlayCount = it.toInt() },
                valueRange = 1f..100f
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text("مدت زمان پخش هر فیلم: ${AutoPlayManager.durationPerVideoSeconds} ثانیه", color = Color.LightGray, fontSize = 12.sp)
            Slider(
                value = AutoPlayManager.durationPerVideoSeconds.toFloat(),
                onValueChange = { AutoPlayManager.durationPerVideoSeconds = it.toInt() },
                valueRange = 20f..300f // ۲۰ ثانیه تا ۵ دقیقه (۳۰۰ ثانیه)
            )
        }
    }
}

// بخش ۶: تغییر زبان برنامه به صورت کاملاً واقعی
@Composable
fun LanguageSelectionSection() {
    val languages = listOf(
        Pair(AppLanguage.FA, "فارسی"),
        Pair(AppLanguage.EN, "English"),
        Pair(AppLanguage.AR, "العربية"),
        Pair(AppLanguage.JA, "日本語"),
        Pair(AppLanguage.ZH, "中文"),
        Pair(AppLanguage.HI, "हिन्दी")
    )

    Column(modifier = Modifier.padding(12.dp)) {
        Text("انتخاب زبان اپلیکیشن / Language:", color = Color.LightGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(languages) { item ->
                FilterChip(
                    selected = AppLanguageManager.currentLanguage == item.first,
                    onClick = { AppLanguageManager.currentLanguage = item.first },
                    label = { Text(item.second, fontSize = 12.sp) }
                )
            }
        }
    }
}

// کارت آکاردئونی بازشونده با حالت + و -
@Composable
fun ExpandableSettingCard(
    title: String,
    content: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = null,
                    tint = AppThemeManager.primaryColor
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Box(modifier = Modifier.background(Color(0xFF222222))) {
                    content()
                }
            }
        }
    }
}
