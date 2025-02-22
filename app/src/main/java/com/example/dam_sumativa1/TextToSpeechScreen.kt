package com.example.dam_sumativa1

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User
import com.example.dam_sumativa1.services.UserService
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun TextToSpeechScreen(navController: NavController, user: User) {
    val context = LocalContext.current
    var textToRead by remember { mutableStateOf("") }
    var savedTexts by remember { mutableStateOf(listOf<String>()) }
    val coroutineScope = rememberCoroutineScope()
    val userService = remember { UserService() }

    val textToSpeech = remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        textToSpeech.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.value?.setLanguage(Locale("es", "ES"))
            }
        }

        userService.obtenerTextos(user.uid!!) { texts ->
            savedTexts = texts
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

        Row {
            Button(onClick = {
                if (textToRead.isNotBlank()) {
                    textToSpeech.value?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }) {
                Text("Reproducir")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (textToRead.isNotBlank()) {
                    coroutineScope.launch {
                        savedTexts = savedTexts + textToRead
                        userService.agregarTexto(user.uid!!, textToRead)
                        textToRead = ""
                    }
                }
            }) {
                Text("Guardar")
            }

        }

        Spacer(modifier = Modifier.height(24.dp))

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
