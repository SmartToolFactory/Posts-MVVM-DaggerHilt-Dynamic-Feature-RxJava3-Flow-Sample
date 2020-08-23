package com.smarttoolfactory.data.source

import com.google.common.truth.Truth
import com.smarttoolfactory.data.api.PostApi
import com.smarttoolfactory.data.db.PostDao
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToObjectList
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class PostDataSourceCoroutinesTest {


    companion object {
        val PostDTOList =
            convertFromJsonToObjectList<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!

        val postEntityList =
            convertFromJsonToObjectList<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class RemotePostDataSourceTest {


        private val postApi = mockk<PostApi>()

        private lateinit var remotePostDataSource: RemotePostDataSource


        @Test
        fun `given network error occurred, should throw Exception`() = runBlockingTest {

            // GIVEN
            coEvery { postApi.getPosts() } throws Exception("Network Exception")

            // WHEN
            val expected = try {
                remotePostDataSource.getPostDTOs()
            } catch (e: Exception) {
                e
            }

            // THEN
            Truth.assertThat(expected).isInstanceOf(Exception::class.java)
            coVerify(exactly = 1) { postApi.getPosts() }

        }


        @Test
        fun `given Http 200, should return postDTO list`() = runBlockingTest {

            // GIVEN
            val actual = PostDTOList
            coEvery { postApi.getPosts() } returns actual

            // WHEN
            val expected = remotePostDataSource.getPostDTOs()

            // THEN
            Truth.assertThat(expected).isEqualTo(actual)
            coVerify(exactly = 1) { postApi.getPosts() }
        }


        @BeforeEach
        fun setUp() {
            remotePostDataSource = RemotePostDataSourceImpl(postApi)
        }

        @AfterEach
        fun tearDown() {
            clearMocks(postApi)
        }

    }

}