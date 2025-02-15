package com.example.dam_sumativa1

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.*

@Composable
fun TextToSpeechScreen(navController: NavController) {
    val context = LocalContext.current
    var textToRead by remember { mutableStateOf("") }
    val textToSpeech = remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        textToSpeech.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.value?.language = Locale("es", "ES")
            } else {
                Log.e("TTS", "Error al inicializar TextToSpeech")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech.value?.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = textToRead,
            onValueChange = { textToRead = it },
            label = { Text("Escribe algo para escuchar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (textToRead.isNotBlank()) {
                    textToSpeech.value?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        ) {
            Text("Reproducir")
        }
    }
}
