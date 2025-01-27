package com.example.dam_sumativa1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(navController: NavController, snackbarHostState: SnackbarHostState, globalScale: MutableState<Float>) {
    var identifier by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding((20 * globalScale.value).dp),
        verticalArrangement = Arrangement
            .Center,
        horizontalAlignment = Alignment
            .CenterHorizontally
    ) {
        Text(
            "Recuperar Contraseña",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("Nombre de usuario o correo", fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            ),
            colors = TextFieldDefaults.colors(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val user = User.buscarUserPorUsername(identifier) ?: User.buscarUserPorEmail(identifier)
                try {
                    if (user != null) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Se ha enviado un correo de recuperación.")
                        }
                    } else {
                        throw Exception("Usuario o correo no encontrado.")
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Error desconocido"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Recuperar contraseña", fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value)
        }

        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver al inicio", fontSize = 16.sp * globalScale.value)
        }
        ZoomControls(globalScale)
    }
}

