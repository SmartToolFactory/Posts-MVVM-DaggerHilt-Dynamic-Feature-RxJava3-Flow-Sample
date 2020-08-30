package com.smarttoolfactory.data.source

import com.google.common.truth.Truth
import com.smarttoolfactory.data.api.PostApiRxJava
import com.smarttoolfactory.data.db.PostDaoRxJava3
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class PostDataSourceRxJava3Test {

    companion object {
        val postDTOList =
            convertFromJsonToListOf<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!

        val postEntityList =
            convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class RemoteDataSourceTest {

        private val postApi = mockk<PostApiRxJava>()

        private lateinit var remotePostDataSource: RemoteDataSourceRxJava3Impl

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
            val actual = postDTOList
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class LocalDataSourceTest {

        private val postDao = mockk<PostDaoRxJava3>()

        private lateinit var localPostDataSource: LocalDataSourceRxJava3Impl

        @Test
        fun `given DB is empty should return an empty list`() = runBlockingTest {

            // GIVEN
            val expected = listOf<PostEntity>()
            every { postDao.getPostsSingle() } returns Single.just(expected)

            // WHEN
            val actual = localPostDataSource.getPostEntities().blockingGet()

            // THEN
            Truth.assertThat(actual).isEmpty()
            verify(exactly = 1) { postDao.getPostsSingle() }
        }

        @Test
        fun `given DB is populated should return data list`() = runBlockingTest {

            // GIVEN
            every { postDao.getPostsSingle() } returns Single.just(postEntityList)

            // WHEN
            val actual = localPostDataSource.getPostEntities().blockingGet()

            // THEN
            Truth.assertThat(actual)
                .containsExactlyElementsIn(postEntityList)
            verify(exactly = 1) { postDao.getPostsSingle() }
        }

        @Test
        fun `given entities, should save entities to DB`() = runBlockingTest {

            // GIVEN
            every { postDao.insert(postEntityList) } returns Completable.complete()

            // WHEN
            val testObserver = localPostDataSource.saveEntities(postEntityList).test()

            // THEN
            testObserver.assertComplete()
                .assertNoErrors()
                .dispose()
            verify(exactly = 1) { postDao.insert(postEntityList) }
        }

        @Test
        fun `given no error should delete entities`() = runBlockingTest {

            // GIVEN
            every { postDao.deleteAll() } returns Completable.complete()

            // WHEN
            val testObserver = localPostDataSource.deletePostEntities().test()

            // THEN
            testObserver.assertComplete()
                .assertNoErrors()
                .dispose()
            verify(exactly = 1) { postDao.deleteAll() }
        }

        @BeforeEach
        fun setUp() {
            localPostDataSource = LocalDataSourceRxJava3Impl(postDao)
        }

        @AfterEach
        fun tearDown() {
            clearMocks(postDao)
        }
    }
}
