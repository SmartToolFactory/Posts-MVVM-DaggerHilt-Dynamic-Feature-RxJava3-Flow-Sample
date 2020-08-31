package com.smarttoolfactory.postdynamichilt

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val postList =
        convertFromJsonToListOf<Post>(getResourceAsText(RESPONSE_JSON_PATH))!!

    @Test
    fun testExtensionFunctions() {
        assertEquals(postList.size, 100)
    }
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.smarttoolfactory.postdynamichilt", appContext.packageName)
    }
}
