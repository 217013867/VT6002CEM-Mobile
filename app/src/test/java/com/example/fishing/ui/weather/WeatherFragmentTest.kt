package com.example.fishing.ui.weather

import com.google.type.DateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.*


class WeatherFragmentTest {

    private var WeatherFragmentTest: WeatherFragment? = null

    @Before
    fun setUp() {
        val date = Mockito.mock(Date::class.java)
        Mockito.`when`(date.time).thenReturn(30L)
        val dt = Mockito.mock(DateTime::class.java)
    }

    @Test
    fun someTest() {
        val doubleTime: Long = WeatherFragmentTest!!.getDoubleTime()
        assertEquals(60, doubleTime)
    }
}