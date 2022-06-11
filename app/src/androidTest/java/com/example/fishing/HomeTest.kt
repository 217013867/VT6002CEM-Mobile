package com.example.fishing

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeInstrumentedTest {

    lateinit var repository: Register

    @Before
    fun prepare() {
        val appContext = InstrumentationRegistry
            .getInstrumentation().targetContext
        repository.createUser(
            email = "123456@gmail.com",
            password = "12345678",
            username = "A12345678"
        )
    }

    @Test
    fun logout() {
        val userId = repository.createUser(
            email = "123456@gmail.com",
            password = "12345678",
            username = "A12345678"
        )
        assertEquals(null, userId)
    }
}