package com.example.fishing

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun evenOdd() {
        val result = evenOdd()
        Assert.assertEquals(1, result)
    }
}