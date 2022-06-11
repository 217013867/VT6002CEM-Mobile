package com.example.fishing.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test


class HomeFragmentTest {
    @Test
    fun getMaxTest() {
        assertEquals(1000, getMax(FloatArray(1001) { 10 * (it + 1F) }))
    }
}

fun getMax(arr: FloatArray): Int {
    var ind = 0
    var min = 0.0f

    for (i in 0..1000) {
        if (arr[i] > min) {
            ind = i
            min = arr[i]
        }
    }
    return ind
}