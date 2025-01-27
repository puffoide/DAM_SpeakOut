package com.example.dam_sumativa1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User


@Composable
fun LoginScreen(navController: NavController, userList: List<User>, loggedInUser: MutableState<User?>, snackbarHostState: SnackbarHostState, globalScale: MutableState<Float>) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberUser by remember { mutableStateOf(false) }

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
            "SpeakOut 💬",
            style = MaterialTheme.typography.displayMedium,
            fontSize = if ((MaterialTheme.typography.displayMedium.fontSize * globalScale.value) > 59.sp) 59.sp
            else MaterialTheme.typography.displayMedium.fontSize * globalScale.value,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier
            .height((40 * globalScale.value).dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = {Text("Nombre de usuario", fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (16 * globalScale.value).dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            ),
            colors = TextFieldDefaults.colors(
                    MaterialTheme.colorScheme.onSurface,
                    MaterialTheme.colorScheme.primary
            ),
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña", fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value)},
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (16 * globalScale.value).dp),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            ),
            colors = TextFieldDefaults.colors(
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.primary
            ),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.size((24 * globalScale.value).dp)
                    )
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = (16 * globalScale.value).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberUser,
                onCheckedChange = { rememberUser = it },
                colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary),
                modifier = Modifier.size((24 * globalScale.value).dp)
            )
            Text(
                text = "Recordar usuario",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            )
        }

        Button(onClick = {
            val user = User.buscarUserPorUsername(username)?.takeIf { it.password == password }
            if (user != null) {
                loggedInUser.value = user
                navController.navigate("home")
            } else {
                errorMessage = "El usuario/contraseña son incorrectos"
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = (16 * globalScale.value).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )) {
            Text("Ingresar", fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value)
        }
        Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value)
        Spacer(modifier = Modifier
            .height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            Arrangement.Center,
            Alignment.CenterVertically

        ) {
            Text("¿Eres nuevo?", color = MaterialTheme.colorScheme.onBackground, fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value)
            TextButton(onClick = { navController.navigate("register")}) {
                Text("Regístrate", fontSize = 16.sp * globalScale.value)
            }
        }

        TextButton(onClick = { navController.navigate("forgot_password")}) {
            Text("¿Olvidaste tu contraseña?", fontSize = 16.sp * globalScale.value)
        }
        Spacer(modifier = Modifier
            .height(8.dp))
        ZoomControls(globalScale)

    }

}
