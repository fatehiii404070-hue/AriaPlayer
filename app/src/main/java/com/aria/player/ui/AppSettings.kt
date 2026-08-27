package com.aria.player.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class AudioQuality { STANDARD, HIGH, ULTRA_HD }
enum class VoiceSensitivity { LOW, MEDIUM, ULTRA_HIGH }

object AppThemeManager {
    var primaryColor by mutableStateOf(Color(0xFF1DB954)) // رنگ پیش‌فرض سبز
}

object AppVoiceManager {
    var isVoiceControlEnabled by mutableStateOf(false)
    var sensitivity by mutableStateOf(VoiceSensitivity.MEDIUM)
}

