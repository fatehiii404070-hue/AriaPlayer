package com.aria.player.ui

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun VoiceControlListener(
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(AppVoiceManager.isVoiceControlEnabled) {
        if (!AppVoiceManager.isVoiceControlEnabled) return@DisposableEffect onDispose { }

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fa-IR")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.forEach { text ->
                    val lower = text.trim().lowercase()
                    if (lower.contains("بالا") || lower.contains("بال") || lower.contains("بالاتر")) {
                        onSwipeUp()
                    } else if (lower.contains("پایین") || lower.contains("پای") || lower.contains("پایی") || lower.contains("پایین تر")) {
                        onSwipeDown()
                    }
                }
                if (AppVoiceManager.isVoiceControlEnabled) {
                    speechRecognizer.startListening(intent)
                }
            }

            override fun onError(error: Int) {
                if (AppVoiceManager.isVoiceControlEnabled) {
                    speechRecognizer.startListening(intent)
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)

        onDispose {
            speechRecognizer.destroy()
        }
    }
}

@Composable
fun GlowingVoiceIndicator() {
    if (!AppVoiceManager.isVoiceControlEnabled) return

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .alpha(alpha)
                .background(Color(0xFF00FF00), shape = CircleShape)
        )
    }
}

