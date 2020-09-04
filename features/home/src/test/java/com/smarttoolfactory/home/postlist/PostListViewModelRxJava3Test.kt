package com.smarttoolfactory.home.postlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth
import com.smarttoolfactory.core.viewstate.Status
import com.smarttoolfactory.domain.model.Post
import com.smarttoolfactory.domain.usecase.GetPostListUseCaseRxJava3
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.rule.RxImmediateSchedulerRule
import com.smarttoolfactory.test_utils.test_observer.test
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostListViewModelRxJava3Test {

    // Run tasks synchronously
    /**
     * Not using this causes java.lang.RuntimeException: Method getMainLooper in android.os.Looper
     * not mocked when `this.observeForever(observer)` is called
     */
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Changes schedulers of this test to specified [Scheduler]
     * Without this rule [Observable.observeOn] with AndroidSchedulers.mainThread
     * returns ExceptionInInitializerError
     */
    @Rule
    @JvmField
    val rxImmediateSchedulerRule = RxImmediateSchedulerRule()

    private val postList =
        convertFromJsonToListOf<Post>(getResourceAsText(RESPONSE_JSON_PATH))!!

    /*
        Mocks
     */
    private val useCase: GetPostListUseCaseRxJava3 = mockk()
    private val savedStateHandle: SavedStateHandle = spyk()

    /**
     * ViewModel to test post list which is SUT
     */
    private lateinit var viewModel: PostListViewModelRxJava3

    @Test
    fun `given exception returned from useCase, should have ViewState ERROR offlineFirst`() {

        // GIVEN
        every {
            useCase.getPostsOfflineFirst()
        } returns Single.error(Exception("Network Exception"))

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
        verify(atMost = 1) { useCase.getPostsOfflineFirst() }
    }

    @Test
    fun `given useCase fetched data, should have ViewState SUCCESS and data offlineFirst`() {

        // GIVEN
        every { useCase.getPostsOfflineFirst() } returns Single.just(postList)

        val testObserver = viewModel.postViewState.test()

        // WHEN
        viewModel.getPosts()

        // THEN
        val viewStates = testObserver.values()
        Truth.assertThat(viewStates.first().status).isEqualTo(Status.LOADING)

        val actual = viewStates.last().data
        Truth.assertThat(actual?.size).isEqualTo(100)
        verify(exactly = 1) { useCase.getPostsOfflineFirst() }
        testObserver.dispose()
    }

    @Test
    fun `given exception returned from useCase, should have ViewState ERROR offlineLast`() {

        // GIVEN
        every {
            useCase.getPostsOfflineLast()
        } returns Single.error(Exception("Network Exception"))

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
        verify(atMost = 1) { useCase.getPostsOfflineLast() }
    }

    @Test
    fun `given useCase fetched data, should have ViewState SUCCESS and data offlineLast`() {

        // GIVEN
        every { useCase.getPostsOfflineLast() } returns Single.just(postList)

        val testObserver = viewModel.postViewState.test()

        // WHEN
        viewModel.refreshPosts()

        // THEN
        val viewStates = testObserver.values()
        Truth.assertThat(viewStates.first().status).isEqualTo(Status.LOADING)

        val actual = viewStates.last().data
        Truth.assertThat(actual?.size).isEqualTo(100)
        verify(exactly = 1) { useCase.getPostsOfflineLast() }
        testObserver.dispose()
    }

    @Before
    fun setUp() {
        viewModel =
            PostListViewModelRxJava3(useCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        clearMocks(useCase, savedStateHandle)
    }
}
