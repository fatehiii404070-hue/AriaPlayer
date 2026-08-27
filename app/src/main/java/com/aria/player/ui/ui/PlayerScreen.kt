package com.aria.player.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerScreen(videoUri: Uri?) {
    var showPicker by remember { mutableStateOf(videoUri == null) }

    if (showPicker) {
        FilePickerScreen { uri ->
            showPicker = false
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "ویدیو انتخاب شد - در حال پخش")
        }
    }
}
