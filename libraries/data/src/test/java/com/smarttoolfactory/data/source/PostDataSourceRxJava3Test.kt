package com.smarttoolfactory.data.source

import com.google.common.truth.Truth
import com.smarttoolfactory.data.api.PostApiRxJava
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToObjectList
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class PostDataSourceRxJava3Test {

    companion object {
        val PostDTOList =
            convertFromJsonToObjectList<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!

        val postEntityList =
            convertFromJsonToObjectList<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class RemoteDataSourceTest {

        private val postApi = mockk<PostApiRxJava>()

        private lateinit var remotePostDataSource: RemotePostDataSourceRxJava3

        @Test
        fun `given network error occurred, should return Single with error`() {

            // GIVEN
            every { postApi.getPostsSingle() } returns Single.error(Exception("Network Exception"))

            // WHEN
            val testObserver = remotePostDataSource.getPostDTOs().test()

            // THEN
            testObserver.assertError {
                it.message == "Network Exception"
            }
            verify(exactly = 1) { postApi.getPostsSingle() }
            testObserver.dispose()
        }

        @Test
        fun `given Http 200, should return DTO list`() {

            // GIVEN
            val actual = PostDTOList
            every { postApi.getPostsSingle() } returns Single.just(actual)

            // WHEN
            val expected = remotePostDataSource.getPostDTOs().blockingGet()

            // THEN
            Truth.assertThat(expected).isEqualTo(actual)
            verify(exactly = 1) { postApi.getPostsSingle() }
        }

        @BeforeEach
        fun setUp() {
            remotePostDataSource = RemoteDataSourceRxJava3Impl(postApi)
        }

        @AfterEach
        fun tearDown() {
            clearMocks(postApi)
        }
    }
}
