package com.smarttoolfactory.domain

import io.mockk.every
import io.mockk.spyk
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val mock = spyk<TestObject>(TestObject(), recordPrivateCalls = true)

    @Test
    fun addition_isCorrect() {

        every { mock["count"]() } returns 5

        assertEquals(4, 2 + 2)
    }
}

class TestObject() {

    private var name = "Hello"

    private var counter = 0

    private fun increaseAndCount(): Int {
        return ++counter
    }
}
