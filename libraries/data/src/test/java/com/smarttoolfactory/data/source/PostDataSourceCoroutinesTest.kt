package com.smarttoolfactory.data.source

import com.google.common.truth.Truth
import com.smarttoolfactory.data.api.PostApi
import com.smarttoolfactory.data.db.PostDaoCoroutines
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class PostDataSourceCoroutinesTest {

    companion object {
        val postDTOList =
            convertFromJsonToListOf<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!

        val postEntityList =
            convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class RemoteDataSourceTest {

        private val postApi = mockk<PostApi>()

        private lateinit var remotePostDataSource: RemotePostDataSourceCoroutinesImpl

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
        fun `given Http 200, should return DTO list`() = runBlockingTest {

            // GIVEN
            val actual = postDTOList
            coEvery { postApi.getPosts() } returns actual

            // WHEN
            val expected = remotePostDataSource.getPostDTOs()

            // THEN
            Truth.assertThat(expected).isEqualTo(actual)
            coVerify(exactly = 1) { postApi.getPosts() }
        }

        @BeforeEach
        fun setUp() {
            remotePostDataSource = RemotePostDataSourceCoroutinesImpl(postApi)
        }

        @AfterEach
        fun tearDown() {
            clearMocks(postApi)
        }
    }

    @Nested
    inner class LocalDataSourceTest {

        private val postDao = mockk<PostDaoCoroutines>()

        private lateinit var localPostDataSource: LocalPostDataSourceCoroutinesImpl

        @Test
        fun `given DB is empty should return an empty list`() = runBlockingTest {

            // GIVEN
            val expected = listOf<PostEntity>()
            coEvery { postDao.getPostList() } returns expected

            // WHEN
            val actual = localPostDataSource.getPostEntities()

            // THEN
            Truth.assertThat(actual).isEmpty()
            coVerify(exactly = 1) { postDao.getPostList() }
        }

        @Test
        fun `given DB is populated should return data list`() = runBlockingTest {

            // GIVEN
            coEvery { postDao.getPostList() } returns postEntityList

            // WHEN
            val actual = localPostDataSource.getPostEntities()

            // THEN
            Truth.assertThat(actual).containsExactlyElementsIn(postEntityList)
            coVerify(exactly = 1) { postDao.getPostList() }
        }

        @Test
        fun `given entities, should save entities to DB`() = runBlockingTest {

            // GIVEN
            val idList = postEntityList.map {
                it.id.toLong()
            }

            coEvery { postDao.insert(postEntityList) } returns idList

            // WHEN
            val result = localPostDataSource.saveEntities(postEntityList)

            // THEN
            Truth.assertThat(result).containsExactlyElementsIn(idList)
            coVerify(exactly = 1) { postDao.insert(postEntityList) }
        }

        @Test
        fun `given no error should delete entities`() = runBlockingTest {

            // GIVEN
            coEvery { postDao.deleteAll() } just runs

            // WHEN
            localPostDataSource.deletePostEntities()

            // THEN
            coVerify(exactly = 1) { postDao.deleteAll() }
        }

        @BeforeEach
        fun setUp() {
            localPostDataSource = LocalPostDataSourceCoroutinesImpl(postDao)
        }

        @AfterEach
        fun tearDown() {
            clearMocks(postDao)
        }
    }
}
