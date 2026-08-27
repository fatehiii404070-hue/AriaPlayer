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
    var selectedUri by remember { mutableStateOf(videoUri) }

    if (selectedUri == null) {
        // اگر ویدیویی انتخاب نشده باشد، صفحه انتخاب فایل را نشان می‌دهد
        FilePickerScreen { uri ->
            selectedUri = uri
        }
    } else {
        // وقتی ویدیو انتخاب شد، این بخش اجرا می‌شود (جایی که پلیر و کنترل‌ها قرار می‌گیرند)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ویدیو با موفقیت انتخاب شد!",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedUri.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { selectedUri = null }) {
                    Text("انتخاب ویدیوی دیگر")
                }
            }
        }
    }
}
