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
}