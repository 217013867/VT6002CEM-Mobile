package com.example.fishing

import android.content.Context
import android.content.SharedPreferences
import org.junit.Test
import org.mockito.Mockito

class LoginTest(val context: Context) {
    fun saveUserId(id: String) {
        val sharedPreferences =
            context.getSharedPreferences(
                "USER_DATA",
                Context.MODE_PRIVATE
            )
        sharedPreferences.edit()
            .putString("USER_ID", id).commit()
    }

    fun getUserId(): String {
        val sharedPreferences =
            context.getSharedPreferences(
                "USER_DATA",
                Context.MODE_PRIVATE
            )
        return sharedPreferences.getString("USER_ID", "")!!
    }

    @Test
    fun saveUserId() {
        //mock class
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        val context = Mockito.mock(Context::class.java)

        //simulate the behaviour of the objects
        Mockito.`when`(context.getSharedPreferences(Mockito.anyString(), Mockito.anyInt()))
            .thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)
        Mockito.`when`(sharedPrefsEditor.putString(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(sharedPrefsEditor)

        val userId = "A12345678"
        val preKey = "USER_ID"

        Mockito.verify(sharedPrefsEditor).putString(
            Mockito.argThat { key -> key == preKey },
            Mockito.argThat { value -> value == userId })
        Mockito.verify(sharedPrefsEditor).commit()
    }
}