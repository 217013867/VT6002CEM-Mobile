package com.example.fishing

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {
    /**
     * Biometric Permission
     */
    private var device: UiDevice? = null

    @get:Rule
    var mainActivityTestRule = (Login::class.java)

    @Before
    fun setUp() {
        this.device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun testBiometricPermissionDenied() {
        val denyButton = this.device?.findObject(UiSelector().text("DENY"))
        val permissionDeniedMessage =
            this.device?.findObject(UiSelector().text("Permission denied"))

        denyButton!!.click()

        assert(permissionDeniedMessage!!.exists())
    }

    @Test
    fun testBiometricPermissionAllowed() {
        val allowButton = this.device?.findObject(UiSelector().text("ALLOW"))
        var permissionAllowedMessage =
            this.device?.findObject(UiSelector().text("Permission allowed"))
        allowButton!!.click()
        assert(permissionAllowedMessage!!.exists())
    }

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
    fun getUserId() {
        val userId = repository.createUser(
            email = "123456@gmail.com",
            password = "12345678",
            username = "A12345678"
        )
        Assert.assertEquals("A12345678", userId)
    }

}