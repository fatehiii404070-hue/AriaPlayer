package com.aria.player.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

enum class AppLanguage { FA, EN, AR, JA, ZH, HI }

object AppLanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.FA)

    fun getTranslation(faText: String, enText: String, arText: String, jaText: String, zhText: String, hiText: String): String {
        return when (currentLanguage) {
            AppLanguage.FA -> faText
            AppLanguage.EN -> enText
            AppLanguage.AR -> arText
            AppLanguage.JA -> jaText
            AppLanguage.ZH -> zhText
            AppLanguage.HI -> hiText
        }
    }
}

object AutoPlayManager {
    var isAutoPlayEnabled by mutableStateOf(false)
    var autoPlayCount by mutableStateOf(40) // تعداد فیلم برای پخش خودکار
    var durationPerVideoSeconds by mutableStateOf(20) // زمان هر فیلم (۲۰ ثانیه تا ۳۰۰ ثانیه)
}

data class CustomFolder(
    val id: String,
    var name: String,
    val iconIndex: Int,
    val videoUris: MutableList<String> = mutableListOf()
)
