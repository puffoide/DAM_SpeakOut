package com.example.dam_sumativa1

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User

@Composable
fun ForgotPasswordScreen(navController: NavController, userList: List<User>) {
    var identifier by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        TextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("Nombre de usuario o correo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = TextFieldDefaults.colors(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val user = User.buscarUserPorUsername(identifier) ?: User.buscarUserPorEmail(identifier)
                if (user != null) {
                    Toast.makeText(
                        context,
                        "Se ha enviado un correo de recuperación.",
                        Toast.LENGTH_LONG
                    ).show()
                    messageError = "" // Limpia errores si encuentra el usuario
                } else {
                    messageError = "Usuario o correo no encontrado."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Recuperar contraseña")
        }

        if (messageError.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(messageError, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Volver al inicio")
        }
    }
}

