package com.example.fishing

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import org.junit.Test

class MainActivityTest {
    @Test
    fun testNavigationToMain() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        // Verify that performing a click changes the NavController’s state
        onView(ViewMatchers.withId(R.id.toolbar)).perform(ViewActions.click())
    }
}