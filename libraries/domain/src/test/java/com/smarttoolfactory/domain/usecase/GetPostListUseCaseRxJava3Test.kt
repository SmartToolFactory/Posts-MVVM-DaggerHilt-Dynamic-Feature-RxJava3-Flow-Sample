package com.smarttoolfactory.domain.usecase

import android.database.SQLException
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.repository.PostRepositoryRxJava3
import com.smarttoolfactory.domain.error.EmptyDataException
import com.smarttoolfactory.domain.mapper.EntityToPostMapper
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.extension.RxImmediateSchedulerExtension
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class GetPostListUseCaseRxJava3Test {

    private val repository: PostRepositoryRxJava3 = mockk()
    private val entityToPostMapper: EntityToPostMapper = mockk()

    companion object {

        @JvmField
        @RegisterExtension
        val rxImmediateSchedulerExtension = RxImmediateSchedulerExtension()

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

    private lateinit var useCase: GetPostListUseCaseRxJava3

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
        fun `given data returned from Remote, Local should delete old, save and return data`() {

            // GIVEN
            every { repository.fetchEntitiesFromRemote() } returns Single.just(postEntityList)
            every { repository.deletePostEntities() } returns Completable.complete()
            every {
                repository.savePostEntities(postEntities = postEntityList)
            } returns Completable.complete()
            every { repository.getPostEntitiesFromLocal() } returns Single.just(postEntityList)
            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = useCase.getPostsOfflineLast().test()

            // THEN
            testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue {
                    it.containsAll(postList)
                }
                .dispose()

            verifySequence {
                repository.fetchEntitiesFromRemote()
                repository.deletePostEntities()
                repository.savePostEntities(postEntityList)
                repository.getPostEntitiesFromLocal()
                entityToPostMapper.map(postEntityList)
            }
        }

        @Test
        fun `given exception returned from Remote source, should fetch data from Local source`() {

            // GIVEN
            every {
                repository.fetchEntitiesFromRemote()
            } returns Single.error(Exception("Network Exception"))
            every { repository.getPostEntitiesFromLocal() } returns Single.just(postEntityList)
            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = useCase.getPostsOfflineLast().test()

            // THEN
            testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue {
                    it.containsAll(postList)
                }
                .dispose()

            verifySequence {
                repository.fetchEntitiesFromRemote()
                repository.getPostEntitiesFromLocal()
                entityToPostMapper.map(postEntityList)
            }
        }

        @Test
        fun `given empty data or null returned from Remote, should fetch data from Local `() {

            // GIVEN
            every { repository.fetchEntitiesFromRemote() } returns Single.just(listOf())
            every { repository.getPostEntitiesFromLocal() } returns Single.just(postEntityList)
            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = useCase.getPostsOfflineLast().test()

            // THEN
            testObserver
                .assertComplete()
                .assertValue {
                    it.containsAll(postList)
                }
                .dispose()

            verifySequence {
                repository.fetchEntitiesFromRemote()
                repository.getPostEntitiesFromLocal()
                entityToPostMapper.map(postEntityList)
            }
        }

        @Test
        fun `given exception returned from Local source, should throw DB exception`() {

            // GIVEN
            every {
                repository.fetchEntitiesFromRemote()
            } returns Single.error(Exception("Network Exception"))

            every {
                repository.getPostEntitiesFromLocal()
            } returns Single.error(SQLException("Database Exception"))

            // WHEN
            val testObserver = useCase.getPostsOfflineLast().test()

            // THEN
            testObserver
                .assertNotComplete()
                .assertError(SQLException::class.java)
                .dispose()

            verifySequence {
                repository.fetchEntitiesFromRemote()
                repository.getPostEntitiesFromLocal()
            }
        }

        @Test
        fun `given Remote error and Local source is empty, should throw EmptyDataException`() {

            // GIVEN
            every {
                repository.fetchEntitiesFromRemote()
            } returns Single.error(Exception("Network Exception"))

            every { repository.getPostEntitiesFromLocal() } returns Single.just(listOf())

            // WHEN
            val testObserver = useCase.getPostsOfflineLast().test()

            // THEN
            testObserver

                .assertError(EmptyDataException::class.java)
                .dispose()

            verifySequence {
                repository.fetchEntitiesFromRemote()
                repository.getPostEntitiesFromLocal()
            }
        }
    }

    @Nested
    @DisplayName("Offline-First Tests")
    inner class OffLineFirstTest {

        @Test
        fun `given Local source has data, should return data`() {

            // GIVEN
            every { repository.getPostEntitiesFromLocal() } returns Single.just(postEntityList)
            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = useCase.getPostsOfflineFirst().test()

            // THEN
            testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue {
                    it.containsAll(postList)
                }
                .dispose()

            coVerifySequence {
                repository.getPostEntitiesFromLocal()
                entityToPostMapper.map(postEntityList)
            }
        }

        @Test
        fun `given Local source is empty, should fetch data from Remote`() {

            // GIVEN
            every { repository.getPostEntitiesFromLocal() } returns Single.just(listOf())
            every { repository.fetchEntitiesFromRemote() } returns Single.just(postEntityList)
            every { repository.deletePostEntities() } returns Completable.complete()
            every {
                repository.savePostEntities(postEntities = postEntityList)
            } returns Completable.complete()
            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = useCase.getPostsOfflineFirst().test()

            // THEN
            testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue {
                    it.containsAll(postList)
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
        fun `given exception returned from Local source should fetch data from Remote`() {

            // GIVEN
            every {
                repository.getPostEntitiesFromLocal()
            } returns Single.error(SQLException("Database Exception"))
            every { repository.fetchEntitiesFromRemote() } returns Single.just(postEntityList)
            every { repository.deletePostEntities() } returns Completable.complete()
            every {
                repository.savePostEntities(postEntities = postEntityList)
            } returns Completable.complete()
            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = useCase.getPostsOfflineFirst().test()

            // THEN
            testObserver
                .assertComplete()
                .assertNoErrors()
                .assertValue {
                    it.containsAll(postList)
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
        fun `given Local source is empty and Remote returned error, should throw exception`() {

            // GIVEN
            every { repository.getPostEntitiesFromLocal() } returns Single.just(listOf())

            every {
                repository.fetchEntitiesFromRemote()
            } returns Single.error(Exception("Network Exception"))

            every { entityToPostMapper.map(postEntityList) } returns postList

            // WHEN
            val testObserver = useCase.getPostsOfflineFirst().test()

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
        useCase = GetPostListUseCaseRxJava3(
            repository,
            entityToPostMapper
        )
    }

    @AfterEach
    fun tearDown() {
        clearMocks(repository, entityToPostMapper)
    }
}
