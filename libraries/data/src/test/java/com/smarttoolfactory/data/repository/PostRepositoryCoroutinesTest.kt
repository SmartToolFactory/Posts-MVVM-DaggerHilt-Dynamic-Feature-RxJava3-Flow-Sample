package com.smarttoolfactory.data.repository

import com.google.common.truth.Truth
import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.source.LocalPostDataSource
import com.smarttoolfactory.data.source.PostDataSourceCoroutinesTest
import com.smarttoolfactory.data.source.RemotePostDataSource
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToObjectList
import com.smarttoolfactory.test_utils.util.getResourceAsText
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryCoroutinesTest {

    private lateinit var repository: PostRepositoryCoroutinesImpl

    private val localPostDataSource: LocalPostDataSource = mockk()
    private val remotePostDataSource: RemotePostDataSource = mockk()
    private val mapper: DTOtoEntityMapper = mockk()

    companion object {
        val postDTOList =
            convertFromJsonToObjectList<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!

        val postEntityList =
            convertFromJsonToObjectList<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @Test
    fun `given network error occurred, should throw Exception`() = runBlockingTest {

        // GIVEN
        coEvery { remotePostDataSource.getPostDTOs() } throws Exception("Network Exception")
        every { mapper.map(postDTOList) } returns postEntityList

        // WHEN
        val expected = try {
            repository.fetchEntitiesFromRemote()
        } catch (e: Exception) {
            e
        }

        // THEN
        Truth.assertThat(expected).isInstanceOf(Exception::class.java)
        coVerify(exactly = 1) { remotePostDataSource.getPostDTOs() }
        verify(exactly = 0) { mapper.map(postDTOList) }
    }

    @Test
    fun `given remote data source return PostDTO list, should return PostEntity list`() =
        runBlockingTest {

            // GIVEN
            coEvery { remotePostDataSource.getPostDTOs() } returns postDTOList
            every { mapper.map(postDTOList) } returns postEntityList

            // WHEN
            val expected = repository.fetchEntitiesFromRemote()

            // THEN
            Truth.assertThat(expected).containsExactlyElementsIn(postEntityList)
            coVerifyOrder {
                remotePostDataSource.getPostDTOs()
                mapper.map(postDTOList)
            }
        }

    @Test
    fun `given DB is empty should return an empty list`() = runBlockingTest {

        // GIVEN
        val expected = listOf<PostEntity>()
        coEvery { localPostDataSource.getPostEntities() } returns expected

        // WHEN
        val actual = repository.getPostEntitiesFromLocal()

        // THEN
        Truth.assertThat(actual).isEmpty()
        coVerify(exactly = 1) { localPostDataSource.getPostEntities() }
    }

    @Test
    fun `given DB is populated should return data list`() = runBlockingTest {

        // GIVEN
        coEvery {
            localPostDataSource.getPostEntities()
        } returns PostDataSourceCoroutinesTest.postEntityList

        // WHEN
        val actual = repository.getPostEntitiesFromLocal()

        // THEN
        Truth.assertThat(actual)
            .containsExactlyElementsIn(PostDataSourceCoroutinesTest.postEntityList)
        coVerify(exactly = 1) { localPostDataSource.getPostEntities() }
    }

    @Test
    fun `given entities, should save entities`() = runBlockingTest {

        // GIVEN
        val idList = PostDataSourceCoroutinesTest.postEntityList.map {
            it.id.toLong()
        }

        coEvery {
            localPostDataSource.saveEntities(PostDataSourceCoroutinesTest.postEntityList)
        } returns idList

        // WHEN
        repository.savePostEntities(PostDataSourceCoroutinesTest.postEntityList)

        // THEN
        coVerify(exactly = 1) {
            localPostDataSource.saveEntities(PostDataSourceCoroutinesTest.postEntityList)
        }
    }

    @Test
    fun `given no error should delete entities`() = runBlockingTest {

        // GIVEN
        coEvery { localPostDataSource.deletePostEntities() } just runs

        // WHEN
        localPostDataSource.deletePostEntities()

        // THEN
        coVerify(exactly = 1) { localPostDataSource.deletePostEntities() }
    }

    @BeforeEach
    fun setUp() {
        repository = PostRepositoryCoroutinesImpl(localPostDataSource, remotePostDataSource, mapper)
    }

    @AfterEach
    fun tearDown() {
        clearMocks(localPostDataSource, remotePostDataSource, mapper)
    }
}
