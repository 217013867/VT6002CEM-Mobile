package com.example.fishing

import org.junit.Assert
import org.junit.Test


class HomeTest {

    lateinit var repository: Register

    @Test
    fun logout() {
        val userId = repository.createUser(
            email = "123456@gmail.com",
            password = "12345678",
            username = "A12345678"
        )
        Assert.assertEquals(null, userId)
    }
}