package com.smarttoolfactory.data

import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.test_utils.util.convertFromJsonToObjectList
import com.smarttoolfactory.test_utils.util.getResourceAsText
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    private val postList by lazy {
        convertFromJsonToObjectList<PostDTO>(getResourceAsText("response.json"))!!
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
