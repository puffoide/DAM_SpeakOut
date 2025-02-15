package com.example.dam_sumativa1

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User
import com.example.dam_sumativa1.utils.manejarResultado
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController, loggedInUser: MutableState<User?>, snackbarHostState: SnackbarHostState, globalScale: MutableState<Float>) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberUser by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val imageId = R.drawable.speakout_icon

    val annotatedString = buildAnnotatedString {
        append("SpeakOut ")
        appendInlineContent("speakoutIcon", "[icon]")
    }
    val maxSize = 26
    val maxSizeIcon = 91
    val iconSize by remember { derivedStateOf { minOf((70 * globalScale.value).toInt(), maxSizeIcon) } }
    val inlineContent = mapOf(
        "speakoutIcon" to InlineTextContent(
            placeholder = Placeholder(
                width = iconSize.sp,
                height = iconSize.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "SpeakOut Icon",
                modifier = Modifier.size(iconSize.dp, iconSize.dp)
            )
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding( minOf((20 * globalScale.value).toInt(), maxSize).dp),
        verticalArrangement = Arrangement
            .Center,
        horizontalAlignment = Alignment
            .CenterHorizontally
    ) {

        Text(
            text = annotatedString,
            inlineContent = inlineContent,
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
            manejarResultado(
                operacion = {
                    val user = User.buscarUserPorUsername(username)?.takeIf { it.password == password }
                    if (user != null) {
                        loggedInUser.value = user
                        true
                    } else {
                        throw Exception("El usuario/contraseña son incorrectos")
                    }
                },
                onSuccess = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Ingreso exitoso!")
                    }
                    navController.navigate("home")
                },
                onError = { mensajeError ->
                    errorMessage = mensajeError
                }
            )
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = (16 * globalScale.value).dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )) {
            Text("Ingresar", fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value)
        }
        if (errorMessage.isNotBlank()) {
            Text(
                errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize * globalScale.value
            )
            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )
        }
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
