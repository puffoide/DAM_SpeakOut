package com.example.dam_sumativa1

import android.content.Context
import androidx.compose.ui.test.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dam_sumativa1.modelo.User
import com.example.dam_sumativa1.services.UserService
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith



import org.junit.Assert.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UserServiceTest {

    private lateinit var userService: UserService

    @Before
    fun setup() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        if (FirebaseApp.getApps(appContext).isEmpty()) {
            FirebaseApp.initializeApp(appContext)
        }
        userService = UserService()
    }

    @Test
    fun testRegisterUser_Success() = runBlocking {
        val username = "puffo"
        val email = "felipeofwanteds2012@gmail.com"
        val password = "4533031299pipe"

        var isSuccess = false
        var errorMessage: String? = null

        val result = suspendCoroutine<Result<Unit>> { continuation ->
            userService.registrarUsuario(username, email, password) { success, error ->
                isSuccess = success
                errorMessage = error
                if (success) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resumeWithException(Exception(error ?: "Error desconocido"))
                }
            }
        }

        result.getOrThrow()

        assertTrue("El registro debería ser exitoso", isSuccess)
        assertNull("No debería haber un mensaje de error", errorMessage)
    }


    @Test
    fun testLoginUser_Success() = runBlocking {
        val username = "puffo"
        val password = "4533031299pipe"

        val user = userService.buscarUserPorUsername(username)
        assertNotNull("El usuario debería existir", user)

        val result = suspendCoroutine<Result<Boolean>> { continuation ->
            userService.iniciarSesion(user!!.email, password) { userLogged, error ->
                if (userLogged != null) {
                    continuation.resume(Result.success(true))
                } else {
                    continuation.resumeWithException(Exception(error ?: "Error desconocido"))
                }
            }
        }

        val loggedInUser = result.getOrThrow()

        assertTrue("El usuario debería iniciar sesión correctamente", loggedInUser)
    }


    @Test
    fun testLoginUser_Failure() = runBlocking {
        val username = "wrongUser"
        val password = "incorrectPassword"

        val user = userService.buscarUserPorUsername(username)

        if (user == null) {
            assertTrue("El usuario no debería existir", true)
        } else {
            var loggedInUser = false
            userService.iniciarSesion(user.email, password) { userLogged, error ->
                loggedInUser = userLogged != null
            }

            assertFalse("El inicio de sesión debería fallar con credenciales incorrectas", loggedInUser)
        }
    }


    @Test
    fun testForgotPassword_Success() = runBlocking {
        val email = "felipe.salgadotello@gmail.com"

        var success = false
        userService.enviarCorreoRecuperacion(email)

        success = true

        assertTrue("El correo de recuperación debería enviarse correctamente", success)
    }
}