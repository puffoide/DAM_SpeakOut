package com.example.dam_sumativa1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dam_sumativa1.modelo.User
import com.example.dam_sumativa1.ui.theme.DAM_Sumativa1Theme

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
    val userList = remember {
        mutableStateOf(
            mutableListOf(
                User("admin", "admin@ejemplo.com", "admin123"),
                User("testuser", "test@ejemplo.com", "test123")
            )
        )
    }
    val loggedInUser = remember { mutableStateOf<User?>(null) }

    Column {
        ThemeToggle(isDarkTheme)
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(navController, userList.value, loggedInUser)
            }
            composable("register") {
                RegisterScreen(navController, userList.value)
            }
            composable("forgot_password") {
                ForgotPasswordScreen(navController, userList.value)
            }
            composable("home") {
                HomeScreen(navController, loggedInUser)
            }
        }
    }
}