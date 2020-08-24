package com.smarttoolfactory.data.repository

import com.google.common.truth.Truth
import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.source.LocalPostDataSourceRxJava3
import com.smarttoolfactory.data.source.RemotePostDataSourceRxJava3
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryRxJava3Test {

    private lateinit var repository: PostRepositoryRxJava3Impl

    private val localPostDataSource: LocalPostDataSourceRxJava3 = mockk()
    private val remotePostDataSource: RemotePostDataSourceRxJava3 = mockk()
    private val mapper: DTOtoEntityMapper = mockk()

    companion object {
        val postDTOList =
            convertFromJsonToListOf<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!

        val postEntityList =
            convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @Test
    fun `given network error occurred, should throw Exception`() {

        // GIVEN
        every {
            remotePostDataSource.getPostDTOs()
        } returns Single.error(Exception("Network Exception"))
        every { mapper.map(postDTOList) } returns postEntityList

        // WHEN
        val testObserver = repository.fetchEntitiesFromRemote().test()

        // THEN
        testObserver
            .assertError(Exception::class.java)
            .assertError {
                it.message == "Network Exception"
            }
            .assertNotComplete()
            .dispose()

        verify(exactly = 1) { remotePostDataSource.getPostDTOs() }
        verify(exactly = 0) { mapper.map(postDTOList) }
    }

    @Test
    fun `given remote data source return PostDTO list, should return PostEntity list`() {

        // GIVEN
        every { remotePostDataSource.getPostDTOs() } returns Single.just(postDTOList)
        every { mapper.map(postDTOList) } returns postEntityList

        // WHEN
        val expected = repository.fetchEntitiesFromRemote().blockingGet()

        // THEN
        Truth.assertThat(expected).containsExactlyElementsIn(postEntityList)
        verifyOrder {
            remotePostDataSource.getPostDTOs()
            mapper.map(postDTOList)
        }
    }

    @Test
    fun `given DB is empty should return an empty list`() {

        // GIVEN
        val expected = listOf<PostEntity>()
        every { localPostDataSource.getPostEntities() } returns Single.just(expected)

        // WHEN
        val actual = repository.getPostEntitiesFromLocal().blockingGet()

        // THEN
        Truth.assertThat(actual).isEmpty()
        verify(exactly = 1) { localPostDataSource.getPostEntities() }
    }

    @Test
    fun `given DB is populated should return data list`() {

        // GIVEN
        every { localPostDataSource.getPostEntities() } returns Single.just(postEntityList)

        // WHEN
        val actual = repository.getPostEntitiesFromLocal().blockingGet()

        // THEN
        Truth.assertThat(actual)
            .containsExactlyElementsIn(postEntityList)
        verify(exactly = 1) { localPostDataSource.getPostEntities() }
    }

    @Test
    fun `given entities, should save entities`() {

        // GIVEN
        every { localPostDataSource.saveEntities(postEntityList) } returns Completable.complete()

        // WHEN
        val testObserver = repository.savePostEntities(postEntityList).test()

        // THEN
        testObserver.assertNoErrors()
            .assertComplete()
            .dispose()
        verify(exactly = 1) { localPostDataSource.saveEntities(postEntityList) }
    }

    @Test
    fun `given no error should delete entities`() {

        // GIVEN
        every { localPostDataSource.deletePostEntities() } returns Completable.complete()

        // WHEN
        val testObserver = localPostDataSource.deletePostEntities().test()

        // THEN
        testObserver.assertNoErrors()
            .assertComplete()
            .dispose()
        verify(exactly = 1) { localPostDataSource.deletePostEntities() }
    }

    @BeforeEach
    fun setUp() {
        repository = PostRepositoryRxJava3Impl(localPostDataSource, remotePostDataSource, mapper)
    }

    @AfterEach
    fun tearDown() {
        clearMocks(localPostDataSource, remotePostDataSource, mapper)
    }
}
