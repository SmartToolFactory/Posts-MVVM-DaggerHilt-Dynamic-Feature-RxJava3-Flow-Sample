package com.smarttoolfactory.data.api

import com.google.common.truth.Truth
import com.smarttoolfactory.data.model.PostDTO
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.HttpURLConnection
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PostApiRxJava3Test : AbstractPostApiTest() {

    /**
     * Api is the SUT to test headers, url, response and DTO objects
     */
    private lateinit var api: PostApiRxJava

    @Test
    fun `Request should have correct url`() {

        // GIVEN
        enqueueResponse(HttpURLConnection.HTTP_OK)

        // WHEN
        api
            .getPostsSingle()
            .blockingGet()

        val request = mockWebServer.takeRequest()

        // THEN
        Truth.assertThat(request.path).isEqualTo("/posts")
    }

    @Test
    fun `Service should return post list`() {

        // GIVEN
        enqueueResponse(HttpURLConnection.HTTP_OK)

        // WHEN
        val expected = api
            .getPostsSingle()
            .blockingGet()

        // THEN
        Truth.assertThat(expected.size).isEqualTo(postList.size)
        Truth.assertThat(expected).containsExactlyElementsIn(postList)
    }

    /**
     * ✅ This test PASSES when [TestObserver] used with [TestObserver.await]
     */
    @Test
    fun `Service should return post list with TestObserver`() {

        // GIVEN
        enqueueResponse(HttpURLConnection.HTTP_OK)

        // WHEN
        val testObserver = api
            .getPostsSingle()
            .test()
            .await()
            // THEN
            .assertComplete()
            .assertNoErrors()
            .assertValue {
                it.size == postList.size
            }

        testObserver.dispose()
    }

    @Test
    fun `Service should return post list with TestObserver blockingSubscribe`() {

        // GIVEN
        enqueueResponse(HttpURLConnection.HTTP_OK)
        val testObserver = TestObserver<List<PostDTO>>()

        // WHEN
        api.getPostsSingle().blockingSubscribe(testObserver)
        val expected = testObserver.values()[0]

        // THEN
        Truth.assertThat(expected.size).isEqualTo(postList.size)
        Truth.assertThat(expected).containsExactlyElementsIn(postList)
        testObserver.dispose()
    }

    /*
       HttpException is wrapped in RuntimeException by RxJava
     */
    @Test
    fun `Server down should return 500 error`() {

        // GIVEN
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        // WHEN
        val exception: RuntimeException =
            Assertions.assertThrows(
                RuntimeException::class.java
            ) {
                api
                    .getPostsSingle()
                    .blockingGet()
            }

        // THEN
        Truth.assertThat(exception).isInstanceOf(HttpException::class.java)
        Truth.assertThat(exception.message).isEqualTo("HTTP 500 Server Error")
    }

    @Test
    fun `When page not found should return 404 error`() {

        // GIVEN
        enqueueResponse(HttpURLConnection.HTTP_NOT_FOUND)
        val testObserver = TestObserver<List<PostDTO>>()

        // WHEN
        api.getPostsSingle().blockingSubscribe(testObserver)

        // THEN
        // Assert that throws HttpException
        testObserver.assertError(HttpException::class.java)

        // Assert that onComplete not called
        testObserver.assertNotComplete()

        testObserver.dispose()
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val okHttpClient = OkHttpClient
            .Builder()
            .build()

        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
            .create(PostApiRxJava::class.java)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }
}
