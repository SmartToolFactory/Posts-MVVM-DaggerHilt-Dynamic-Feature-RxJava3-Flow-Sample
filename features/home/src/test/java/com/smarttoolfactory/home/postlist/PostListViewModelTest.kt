package com.smarttoolfactory.home.postlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseFlow
import com.smarttoolfactory.home.viewmodel.PostListViewModelFlow
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.rule.TestCoroutineRule
import com.smarttoolfactory.test_utils.test_observer.test
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostListViewModelTest {

    // Run tasks synchronously
    /**
     * Not using this causes java.lang.RuntimeException: Method getMainLooper in android.os.Looper
     * not mocked when `this.observeForever(observer)` is called
     */
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Rule for testing Coroutines with [TestCoroutineScope] and [TestCoroutineDispatcher]
     */
    @Rule
    @JvmField
    var testCoroutineRule = TestCoroutineRule()

    private val postList =
        convertFromJsonToListOf<Post>(getResourceAsText(RESPONSE_JSON_PATH))!!

    /*
        Mocks
     */
    private val useCase: GetPostListUseCaseFlow = mockk()
    private val savedStateHandle: SavedStateHandle = spyk()

    /**
     * ViewModel to test post list which is SUT
     */
    private lateinit var viewModel: PostListViewModelFlow

    @Test
    fun `given exception returned from useCase, should have ViewState ERROR offlineFirst`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            every {
                useCase.getPostFlowOfflineFirst()
            } returns flow<List<Post>> {
                emit(throw Exception("Network Exception"))
            }

            val testObserver = viewModel.postViewState.test()

            // WHEN
            viewModel.getPosts()

            // THEN
            testObserver
                .assertValue { states ->
                    (
                        states[0].status == Status.LOADING &&
                            states[1].status == Status.ERROR
                        )
                }

            val finalState = testObserver.values()[1]
            Truth.assertThat(finalState.error?.message).isEqualTo("Network Exception")
            Truth.assertThat(finalState.error).isInstanceOf(Exception::class.java)
            verify(atMost = 1) { useCase.getPostFlowOfflineFirst() }
        }

    @Test
    fun `given useCase fetched data, should have ViewState SUCCESS and data offlineFirst`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            every { useCase.getPostFlowOfflineFirst() } returns flow<List<Post>> {
                emit(postList)
            }

            val testObserver = viewModel.postViewState.test()

            // WHEN
            viewModel.getPosts()

            // THEN
            val viewStates = testObserver.values()
            Truth.assertThat(viewStates.first().status).isEqualTo(Status.LOADING)

            val actual = viewStates.last().data
            Truth.assertThat(actual?.size).isEqualTo(100)
            verify(exactly = 1) { useCase.getPostFlowOfflineFirst() }
            testObserver.dispose()
        }

    @Test
    fun `given exception returned from useCase, should have ViewState ERROR offlineLast`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            every {
                useCase.getPostFlowOfflineLast()
            } returns flow<List<Post>> {
                emit(throw Exception("Network Exception"))
            }

            val testObserver = viewModel.postViewState.test()

            // WHEN
            viewModel.refreshPosts()

            // THEN
            testObserver
                .assertValue { states ->
                    (
                        states[0].status == Status.LOADING &&
                            states[1].status == Status.ERROR
                        )
                }
                .dispose()

            val finalState = testObserver.values()[1]
            Truth.assertThat(finalState.error?.message).isEqualTo("Network Exception")
            Truth.assertThat(finalState.error).isInstanceOf(Exception::class.java)
            verify(atMost = 1) { useCase.getPostFlowOfflineLast() }
        }

    @Test
    fun `given useCase fetched data, should have ViewState SUCCESS and data offlineLast`() =
        testCoroutineRule.runBlockingTest {

            // GIVEN
            every { useCase.getPostFlowOfflineLast() } returns flow<List<Post>> {
                emit(postList)
            }

            val testObserver = viewModel.postViewState.test()

            // WHEN
            viewModel.refreshPosts()

            // THEN
            val viewStates = testObserver.values()
            Truth.assertThat(viewStates.first().status).isEqualTo(Status.LOADING)

            val actual = viewStates.last().data
            Truth.assertThat(actual?.size).isEqualTo(100)
            verify(exactly = 1) { useCase.getPostFlowOfflineLast() }
            testObserver.dispose()
        }

    @Before
    fun setUp() {
        viewModel =
            PostListViewModelFlow(testCoroutineRule.testCoroutineScope, useCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        clearMocks(useCase, savedStateHandle)
    }
}
