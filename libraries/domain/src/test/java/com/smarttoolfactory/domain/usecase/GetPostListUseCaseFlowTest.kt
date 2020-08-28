package com.smarttoolfactory.domain.usecase

import android.database.SQLException
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.repository.PostRepositoryCoroutines
import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
import com.smarttoolfactory.domain.error.EmptyDataException
import com.smarttoolfactory.domain.mapper.EntityToPostMapper
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.extension.TestCoroutineExtension
import com.smarttoolfactory.test_utils.test_observer.test
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * Used single instance of tests instead of creating new instance for each test.
 *
 * @see <a href="https://phauer.com/2018/best-practices-unit-testing-kotlin/">Kotlin Test Performance</a>
 *
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GetPostListUseCaseFlowTest {

    private val repository: PostRepositoryCoroutines = mockk()
    private val entityToPostMapper: EntityToPostMapper = mockk()

    private val dispatcherProvider: UseCaseDispatchers =
        UseCaseDispatchers(Dispatchers.Main, Dispatchers.Main, Dispatchers.Main)

    companion object {
        @JvmField
        @RegisterExtension
        val testCoroutineExtension = TestCoroutineExtension()

        val testCoroutineScope = testCoroutineExtension.testCoroutineScope

        /*
            Mock Post data
         */
        private val postList =
            convertFromJsonToListOf<Post>(getResourceAsText(RESPONSE_JSON_PATH))!!

        /*
            Mock PostEntity data
         */
        private val postEntityList =
            convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    private lateinit var useCase: GetPostListUseCaseFlow

    /***
     *
     *   Test class for testing offline-last data fetch with [GetPostListUseCaseFlow]
     *
     *  * Offline Last Scenarios
     *
     * * Check out Remote Source first
     * * If empty data or null returned throw empty set exception
     * * If error occurred while fetching data from remote: Try to fetch data from db
     * * If data is fetched from remote source: delete old data, save new data and return new data
     * * If both network and db don't have any data throw empty set exception
     *
     */
    @Nested
    @DisplayName("Offline-Last(Refresh) Tests")
    inner class OffLineLastTest {

        @Test
        fun `given data returned from Remote, Local should delete old, save and return data`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery { repository.fetchEntitiesFromRemote() } returns postEntityList
                coEvery { repository.deletePostEntities() } just runs
                coEvery { repository.savePostEntities(postEntities = postEntityList) } just runs
                coEvery { repository.getPostEntitiesFromLocal() } returns postEntityList
                coEvery { entityToPostMapper.map(postEntityList) } returns postList

                // WHEN
                val testObserver = useCase.getPostFlowOfflineLast().test(this)

                // THEN
                testObserver
                    .assertComplete()
                    .assertNoErrors()
                    .assertValueAt(0, postList)
                    .assertValues {
                        it.first().containsAll(postList)
                    }
                    .dispose()

                coVerifyOrder {
                    repository.fetchEntitiesFromRemote()
                    repository.deletePostEntities()
                    repository.savePostEntities(postEntityList)
                    repository.getPostEntitiesFromLocal()
                    entityToPostMapper.map(postEntityList)
                }
            }

        @Test
        fun `given exception returned from Remote source, should fetch data from Local source`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery {
                    repository.fetchEntitiesFromRemote()
                } throws Exception("Network Exception")
                coEvery { repository.deletePostEntities() } just runs
                coEvery { repository.savePostEntities(postEntities = postEntityList) } just runs
                coEvery { repository.getPostEntitiesFromLocal() } returns postEntityList
                coEvery { entityToPostMapper.map(postEntityList) } returns postList

                // WHEN
                val testObserver = useCase.getPostFlowOfflineLast().test(this)

                // THEN
                testObserver
                    .assertComplete()
                    .assertNoErrors()
                    .assertValues {
                        it.first().containsAll(postList)
                    }
                    .dispose()

                coVerify(exactly = 1) { repository.fetchEntitiesFromRemote() }
                coVerify(exactly = 1) { repository.getPostEntitiesFromLocal() }
                coVerify(exactly = 0) { repository.deletePostEntities() }
                coVerify(exactly = 0) { repository.savePostEntities(postEntityList) }
                verify(exactly = 1) { entityToPostMapper.map(postEntityList) }
            }

        @Test
        fun `given empty data or null returned from Remote, should fetch data from Local `() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery { repository.fetchEntitiesFromRemote() } returns listOf()
                coEvery { repository.deletePostEntities() } just runs
                coEvery { repository.savePostEntities(postEntities = postEntityList) } just runs
                coEvery { repository.getPostEntitiesFromLocal() } returns postEntityList
                coEvery { entityToPostMapper.map(postEntityList) } returns postList

                // WHEN
                val testObserver = useCase.getPostFlowOfflineLast().test(this)

                // THEN
                testObserver
                    .assertComplete()
                    .assertNoErrors()
                    .assertValues {
                        it.first().containsAll(postList)
                    }
                    .dispose()

                coVerify(exactly = 1) { repository.fetchEntitiesFromRemote() }
                coVerify(exactly = 1) { repository.getPostEntitiesFromLocal() }
                coVerify(exactly = 0) { repository.deletePostEntities() }
                coVerify(exactly = 0) { repository.savePostEntities(postEntityList) }

                verify(exactly = 1) { entityToPostMapper.map(postEntityList) }
            }

        @Test
        fun `given exception returned from Local source, should throw DB exception`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery {
                    repository.fetchEntitiesFromRemote()
                } throws Exception("Network Exception")
                coEvery { repository.deletePostEntities() } just runs
                coEvery { repository.savePostEntities(postEntities = postEntityList) } just runs
                coEvery {
                    repository.getPostEntitiesFromLocal()
                } throws SQLException("Database Exception")
                coEvery { entityToPostMapper.map(postEntityList) } returns postList

                // WHEN
                val testObserver = useCase.getPostFlowOfflineLast().test(this)

                // THEN
                testObserver
                    .assertNotComplete()
                    .assertError(SQLException::class.java)
                    .dispose()

                coVerify(exactly = 1) { repository.fetchEntitiesFromRemote() }
                coVerify(exactly = 1) { repository.getPostEntitiesFromLocal() }
                coVerify(exactly = 0) { repository.deletePostEntities() }
                coVerify(exactly = 0) { repository.savePostEntities(postEntityList) }

                verify(exactly = 0) { entityToPostMapper.map(postEntityList) }
            }

        @Test
        fun `given Remote error and Local source is empty, should throw EmptyDataException`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery {
                    repository.fetchEntitiesFromRemote()
                } throws Exception("Network Exception")

                coEvery { repository.deletePostEntities() } just runs
                coEvery { repository.savePostEntities(postEntities = postEntityList) } just runs
                coEvery { repository.getPostEntitiesFromLocal() } returns listOf()
                coEvery { entityToPostMapper.map(postEntityList) } returns postList

                // WHEN
                val testObserver = useCase.getPostFlowOfflineLast().test(this)

                // THEN
                testObserver
                    .assertNotComplete()
                    .assertError(EmptyDataException::class.java)
                    .dispose()

                coVerify(exactly = 1) { repository.fetchEntitiesFromRemote() }
                coVerify(exactly = 1) { repository.getPostEntitiesFromLocal() }
                coVerify(exactly = 0) { repository.deletePostEntities() }
                coVerify(exactly = 0) { repository.savePostEntities(postEntityList) }

                verify(exactly = 0) { entityToPostMapper.map(postEntityList) }
            }
    }

    @Nested
    @DisplayName("Offline-First Tests")
    inner class OffLineFirstTest {

        @Test
        fun `given Local source has data, should return data`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery { repository.getPostEntitiesFromLocal() } returns postEntityList
                coEvery { entityToPostMapper.map(postEntityList) } returns postList
                // WHEN
                val testObserver = useCase.getPostFlowOfflineFirst().test(this)

                // THEN
                testObserver
                    .assertComplete()
                    .assertNoErrors()
                    .assertValues {
                        it.first().containsAll(postList)
                    }
                    .dispose()

                coVerifySequence {
                    repository.getPostEntitiesFromLocal()
                    entityToPostMapper.map(postEntityList)
                }
            }

        @Test
        fun `given Local source is empty, should fetch data from Remote`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery { repository.getPostEntitiesFromLocal() } returns listOf()
                coEvery { repository.fetchEntitiesFromRemote() } returns postEntityList
                coEvery { repository.deletePostEntities() } just runs
                coEvery { repository.savePostEntities(postEntities = postEntityList) } just runs
                coEvery { entityToPostMapper.map(postEntityList) } returns postList
                // WHEN
                val testObserver = useCase.getPostFlowOfflineFirst().test(this)

                // THEN
                testObserver
                    .assertComplete()
                    .assertNoErrors()
                    .assertValues {
                        it.first().containsAll(postList)
                    }
                    .dispose()

                coVerifySequence {
                    repository.getPostEntitiesFromLocal()
                    repository.fetchEntitiesFromRemote()
                    repository.deletePostEntities()
                    repository.savePostEntities(postEntities = postEntityList)
                    entityToPostMapper.map(postEntityList)
                }
            }

        @Test
        fun `given exception returned from Local source should fetch data from Remote`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery {
                    repository.getPostEntitiesFromLocal()
                } throws SQLException("Database Exception")
                coEvery { repository.fetchEntitiesFromRemote() } returns postEntityList
                coEvery { repository.deletePostEntities() } just runs
                coEvery { repository.savePostEntities(postEntities = postEntityList) } just runs
                coEvery { entityToPostMapper.map(postEntityList) } returns postList

                // WHEN
                val testObserver = useCase.getPostFlowOfflineFirst().test(this)

                // THEN
                testObserver
                    .assertComplete()
                    .assertNoErrors()
                    .assertValues {
                        it.first().containsAll(postList)
                    }
                    .dispose()

                coVerifySequence {
                    repository.getPostEntitiesFromLocal()
                    repository.fetchEntitiesFromRemote()
                    repository.deletePostEntities()
                    repository.savePostEntities(postEntities = postEntityList)
                    entityToPostMapper.map(postEntityList)
                }
            }

        @Test
        fun `given Local source is empty and Remote returned error, should throw exception`() =
            testCoroutineExtension.runBlockingTest {

                // GIVEN
                coEvery { repository.getPostEntitiesFromLocal() } returns listOf()

                coEvery {
                    repository.fetchEntitiesFromRemote()
                } throws Exception("Network Exception")

                coEvery { entityToPostMapper.map(postEntityList) } returns postList

                // WHEN
                val testObserver = useCase.getPostFlowOfflineFirst().test(this)

                // THEN
                testObserver
                    .assertNotComplete()
                    .assertError(EmptyDataException::class.java)
                    .dispose()

                coVerifySequence {
                    repository.getPostEntitiesFromLocal()
                    repository.fetchEntitiesFromRemote()
                }
            }
    }

    @BeforeEach
    fun setUp() {
        useCase = GetPostListUseCaseFlow(
            repository,
            entityToPostMapper,
            dispatcherProvider
        )
    }

    @AfterEach
    fun tearDown() {
        clearMocks(repository, entityToPostMapper)
    }
}
