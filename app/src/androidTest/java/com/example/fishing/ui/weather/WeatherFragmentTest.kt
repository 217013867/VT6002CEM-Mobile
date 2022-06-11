package com.example.fishing.ui.weather

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherFragmentInstrumentedTest {
    /**
     * Location Permission
     */
    private var device: UiDevice? = null

    @get:Rule
    var mainActivityTestRule = (WeatherFragment::class.java)

    @Before
    fun setUp() {
        this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testLocationPermissionDenied() {
        val denyButton = this.device?.findObject(UiSelector().text("DENY"))
        val permissionDeniedMessage =
            this.device?.findObject(UiSelector().text("Permission denied"))

        denyButton!!.click()

        assert(permissionDeniedMessage!!.exists())
    }

    @Test
    fun testLocationPermissionAllowed() {
        val allowButton = this.device?.findObject(UiSelector().text("ALLOW"))
        var permissionAllowedMessage =
            this.device?.findObject(UiSelector().text("Permission allowed"))
        allowButton!!.click()
        assert(permissionAllowedMessage!!.exists())
    }
}