package com.example.dam_sumativa1

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dam_sumativa1.modelo.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, loggedInUser: MutableState<User?>, snackbarHostState: SnackbarHostState, globalScale: MutableState<Float>
) {
    val coroutineScope = rememberCoroutineScope()
    val user = loggedInUser.value
    if (user == null) {
        navController.navigate("login")
        return
    }

    var showProfileDetails by remember { mutableStateOf(false) }
    var selectedScreen by remember { mutableStateOf("Bienvenido, ${user.username}") }
    val topBarTitle = if (showProfileDetails) {
        "Bienvenido, ${user.username}"
    } else {
        selectedScreen
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                actions = {
                    IconButton(onClick = { showProfileDetails = !showProfileDetails }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = selectedScreen,
                onScreenSelected = {
                    selectedScreen = it
                    showProfileDetails = false
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding((20 * globalScale.value).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = showProfileDetails,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Correo: ${user.email}",
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                loggedInUser.value = null
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Registro exitoso")
                                }
                                navController.navigate("login")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Cerrar Sesión", fontSize = MaterialTheme.typography.bodyLarge.fontSize * globalScale.value)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedScreen) {
                "Texto a voz" -> TextToSpeechScreen(navController)
                "Voz a texto" -> VoiceRecorderScreen(navController)
                "Buscar dispositivo" -> MapaUbicacionScreen(navController)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: String, onScreenSelected: (String) -> Unit) {
    NavigationBar {
        listOf(
            "Texto a voz" to Icons.Default.Mic,
            "Voz a texto" to Icons.Default.Edit,
            "Buscar dispositivo" to Icons.Default.LocationOn
        ).forEach { (route, icon) ->
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = null) },
                label = { Text(route) },
                selected = selectedScreen == route,
                onClick = { onScreenSelected(route) } // 🔹 Cierra el perfil al cambiar de pantalla
            )
        }
    }
}