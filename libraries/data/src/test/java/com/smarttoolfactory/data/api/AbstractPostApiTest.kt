package com.smarttoolfactory.data.api

import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import java.io.IOException
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

/**
 * Abstract class fo testing api with [MockWebServer] and [JUnit5]
 */
abstract class AbstractPostApiTest {

    lateinit var mockWebServer: MockWebServer

    private val responseAsString by lazy {
        getResourceAsText(RESPONSE_JSON_PATH)
    }

    val postList by lazy {
        convertFromJsonToListOf<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @BeforeEach
    open fun setUp() {

        println("AbstractPostApiTest SET UP")

        mockWebServer = MockWebServer()
            .apply {
                start()
//                dispatcher = PostDispatcher()
            }
    }

    @AfterEach
    open fun tearDown() {
        println("AbstractPostApiTest TEAR DOWN")
        mockWebServer.shutdown()
    }

    @Throws(IOException::class)
    fun enqueueResponse(
        code: Int = 200,
        headers: Map<String, String>? = null
    ): MockResponse {

        // Define mock response
        val mockResponse = MockResponse()
        // Set response code
        mockResponse.setResponseCode(code)

        // Set headers
        headers?.let {
            for ((key, value) in it) {
                mockResponse.addHeader(key, value)
            }
        }

        // Set body
        mockWebServer.enqueue(
            mockResponse.setBody(responseAsString)
        )
        println(
            "🍏 enqueueResponse() ${Thread.currentThread().name}," +
                " ${responseAsString.length} $mockResponse"
        )
        return mockResponse
    }

//    inner class PostDispatcher : QueueDispatcher() {
//
//        override fun dispatch(request: RecordedRequest): MockResponse {
//
//            return if (request.getHeader("Build") != null) {
//                enqueueResponse(HttpURLConnection.HTTP_OK)
//            } else {
//                MockResponse()
//                    .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
//                    .setBody("SERVER_INTERNAL_ERROR_MESSAGE")
//            }
//        }
//
//    }
}
