package com.example.fishing

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

class MainActivityTest {
    @Test
    fun onCreate() {
        lateinit var mainActivitySpy: MainActivity

        @Before
        fun setUp() {
            mainActivitySpy = spy(MainActivity())
        }

        @Test
        fun `Test case`() {
            mainActivitySpy.onCreate(TODO())

            verify(mainActivitySpy).finish()
        }
    }
}