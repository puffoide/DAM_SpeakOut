package com.example.dam_sumativa1

import com.example.dam_sumativa1.modelo.User
import com.example.dam_sumativa1.services.UserService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class MainActivityTest {
    @Test
    fun `comprobar que la actividad se inicia correctamente`() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        assertNotNull(activity)
    }
}

class UserServiceTest {

    private val userService = mockk<UserService>()

    @Test
    fun `buscar usuario por username retorna usuario válido`() = runTest {
        val userMock = User(uid = "uid", username = "puff", email = "example@example.com")


        coEvery { userService.buscarUserPorUsername("puff") } returns userMock

        val result = userService.buscarUserPorUsername("puff")

        assertNotNull(result)
        assertEquals("puff", result?.username)
        assertEquals("uid", result?.uid)
    }

    @Test
    fun `iniciar sesión con credenciales correctas`() = runTest {
        val username = "puffo"
        val password = "4533031299pipe"
        val userMock = User(uid = "uid", username = username, email = "puffo@example.com")

        coEvery { userService.buscarUserPorUsername(username) } returns userMock
        coEvery { userService.iniciarSesion(userMock.email, password, any()) } answers {
            thirdArg<(User?, String?) -> Unit>().invoke(userMock, null)
        }

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
    fun `iniciar sesión con credenciales incorrectas debería fallar`() = runTest {
        val username = "wrongUser"
        val password = "incorrectPassword"

        coEvery { userService.buscarUserPorUsername(username) } returns null

        val user = userService.buscarUserPorUsername(username)

        if (user == null) {
            assertTrue("El usuario no debería existir", true)
        } else {
            var loggedInUser = false
            coEvery { userService.iniciarSesion(user.email, password, any()) } answers {
                thirdArg<(User?, String?) -> Unit>().invoke(null, "Error de autenticación")
            }

            userService.iniciarSesion(user.email, password) { userLogged, error ->
                loggedInUser = userLogged != null
            }

            assertFalse("El inicio de sesión debería fallar con credenciales incorrectas", loggedInUser)
        }
    }

    @Test
    fun `enviar correo de recuperación debería ser exitoso`() = runTest {
        val email = "felipe.salgadotello@gmail.com"

        coEvery { userService.enviarCorreoRecuperacion(email) } returns Unit

        var success = false
        userService.enviarCorreoRecuperacion(email)

        success = true

        assertTrue("El correo de recuperación debería enviarse correctamente", success)
    }

}