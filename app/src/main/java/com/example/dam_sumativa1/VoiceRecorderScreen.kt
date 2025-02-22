package com.example.dam_sumativa1

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User
import com.example.dam_sumativa1.services.UserService
import kotlinx.coroutines.launch

@Composable
fun VoiceRecorderScreen(navController: NavController, user: User) {
    val context = LocalContext.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    var speechText by remember { mutableStateOf("Presione el botón y hable") }
    var isListening by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val userService = remember { UserService() }
    var savedTexts by remember { mutableStateOf(listOf<String>()) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        userService.obtenerTextos(user.uid!!) { texts ->
            savedTexts = texts
        }
    }


    val micColor by animateColorAsState(
        targetValue = when {
            isListening -> Color.Red
            isProcessing -> Color.Gray
            else -> MaterialTheme.colorScheme.primary
        },
        label = "Mic Color Animation"
    )

    val micSize by animateFloatAsState(
        targetValue = if (isListening) 64f else 55f,
        label = "Mic Size Animation"
    )

    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && !isProcessing) {
            isProcessing = true
            speechRecognizer.startListening(recognizerIntent)
        } else if (!isGranted) {
            Toast.makeText(context, "Permisos del micrófono denegados", Toast.LENGTH_LONG).show()
        }
    }

    val recognizerListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d("SpeechRecognizer", "Listo para escuchar!")
            isListening = true
        }

        override fun onBeginningOfSpeech() {
            speechText = "Escuchando..."
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            speechText = "Procesando..."
            isListening = false
        }

        override fun onError(error: Int) {
            speechText = "Error al reconocer la voz"
            isListening = false
            isProcessing = false
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            speechText = matches?.firstOrNull() ?: "No se pudo reconocer"

            coroutineScope.launch {
                userService.agregarTexto(user.uid!!, speechText)
                userService.obtenerTextos(user.uid) { texts ->
                    savedTexts = texts + speechText
                }
            }

            isListening = false
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    speechRecognizer.setRecognitionListener(recognizerListener)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = speechText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        IconButton(
            onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
            modifier = Modifier
                .size(micSize.dp)
                .padding(8.dp),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = micColor),
            enabled = !isProcessing
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = "Micrófono",
                tint = Color.White
            )
        }
        LazyColumn {
            items(savedTexts) { text ->
                EditableCard(
                    text = text,
                    onDelete = {
                        coroutineScope.launch {
                            savedTexts = savedTexts.filter { it != text }
                            userService.eliminarTexto(user.uid!!, text)
                        }
                    },
                    onEdit = { newText ->
                        coroutineScope.launch {
                            savedTexts = savedTexts.map { if (it == text) newText else it }
                            userService.actualizarTexto(user.uid!!, text, newText)
                        }
                    }
                )
            }
        }
    }
}

