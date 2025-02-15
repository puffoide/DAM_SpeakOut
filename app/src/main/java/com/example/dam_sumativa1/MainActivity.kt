package com.example.dam_sumativa1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dam_sumativa1.modelo.User
import com.example.dam_sumativa1.services.UserService
import com.example.dam_sumativa1.ui.theme.DAM_Sumativa1Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme = remember { mutableStateOf(false) }
            DAM_Sumativa1Theme(darkTheme = isDarkTheme.value) {
                AppNavigation(isDarkTheme)
            }
        }
    }
}

@Composable
fun AppNavigation(isDarkTheme: MutableState<Boolean>) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val globalScale = remember { mutableStateOf(1f) }
    val loggedInUser = remember { mutableStateOf<User?>(null) }


    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            ThemeToggle(isDarkTheme)
            NavHost(navController = navController, startDestination = "login") {
                composable("login") {
                    LoginScreen(navController, loggedInUser, snackbarHostState, globalScale)
                }
                composable("register") {
                    RegisterScreen(navController, snackbarHostState, globalScale)
                }
                composable("forgot_password") {
                    ForgotPasswordScreen(navController, snackbarHostState, globalScale)
                }
                composable("home") {
                    HomeScreen(navController, loggedInUser, snackbarHostState, globalScale)
                }
            }
        }
    }
}
