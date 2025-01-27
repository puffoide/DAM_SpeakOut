package com.example.dam_sumativa1

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User

@Composable
fun RegisterScreen(navController: NavController, userList: MutableList<User>, globalScale: MutableState<Float>) {
    val keyBoardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
            "Registro de Usuario",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario", fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            ),
            colors = TextFieldDefaults.colors(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.primary
            )
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico", fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            ),
            colors = TextFieldDefaults.colors(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.primary
            )
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            ),
            colors = TextFieldDefaults.colors(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.primary
            )
        )

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña", fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                keyBoardController?.hide()
                when {
                    username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                        errorMessage = "Todos los campos son obligatorios"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        errorMessage = "Formato de correo no válido"
                    User.buscarUserPorUsername(username) != null || User.buscarUserPorEmail(email) != null ->
                        errorMessage = "El usuario o correo ya existe"
                    !validatePassword(password) ->
                        errorMessage = "La contraseña debe tener al menos 8 caracteres, incluir letras y números."
                    password != confirmPassword ->
                        errorMessage = "Las contraseñas no coinciden"
                    else -> {
                        User.agregarUser(User(username, email, password))
                        Toast.makeText(context, "Registro exitoso!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Registrarse", fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value)
        }

        Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes cuenta?", color = MaterialTheme.colorScheme.onBackground, fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value)
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Inicia sesión", fontSize = 16.sp * globalScale.value)
            }
        }
        ZoomControls(globalScale)
    }
}

fun validatePassword(password: String): Boolean {
    val hasLetter = password.any { it.isLetter() }
    val hasDigit = password.any { it.isDigit() }
    val hasMinLength = password.length >= 8
    return hasLetter && hasDigit && hasMinLength
}

