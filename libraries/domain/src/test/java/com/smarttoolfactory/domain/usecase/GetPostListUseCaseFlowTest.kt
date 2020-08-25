package com.smarttoolfactory.domain.usecase

import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.repository.PostRepositoryCoroutines
import com.smarttoolfactory.domain.dispatcher.UseCaseDispatchers
import com.smarttoolfactory.domain.mapper.EntityToPostMapper
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.extension.TestCoroutineExtension
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
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

    /*
       Offline Last Scenarios

       * Check out Remote Source first
       * If empty data or null returned throw empty set exception
       * If error occurred while fetching data from remote: Try to fetch data from db
       * If data is fetched from remote source: delete old data, save new data and return new data
       * If both network and db don't have any data throw empty set exception

    */
    @Test
    fun `given list returned from Remote , Local should delete old, save and return Entity list`() =
        testCoroutineExtension.runBlockingTest {

            // GIVEN
            coEvery { repository.fetchEntitiesFromRemote() } returns postEntityList
            coEvery { repository.deletePostEntities() }
            coEvery { repository.savePostEntities(postEntities = postEntityList) }
            coEvery { repository.getPostEntitiesFromLocal() } returns postEntityList

            // WHEN
            val flow = useCase.getPostFlowOfflineLast()

            // THEN
            flow.collect {
                println("List: $it")
            }
        }

    @Test
    fun `given exception returned from Remote source, should check out Local source`() =
        testCoroutineExtension.runBlockingTest {

            // GIVEN

            // WHEN

            // THEN
        }

    @Test
    fun `given empty data or null returned from Remote source, should check out Local source`() =
        testCoroutineExtension.runBlockingTest {

            // GIVEN

            // WHEN

            // THEN
        }

    @Test
    fun `given exception returned from Local source, should return an empty list`() =
        testCoroutineExtension.runBlockingTest {

            // GIVEN

            // WHEN

            // THEN
        }

    @Test
    fun `given both Remote and Local source empty should return an empty list`() =
        testCoroutineExtension.runBlockingTest {

            // GIVEN

            // WHEN

            // THEN
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
