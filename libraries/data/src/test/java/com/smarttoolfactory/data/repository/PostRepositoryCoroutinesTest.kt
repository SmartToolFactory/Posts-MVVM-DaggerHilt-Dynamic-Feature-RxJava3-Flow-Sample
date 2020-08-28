package com.smarttoolfactory.data.repository

import com.google.common.truth.Truth
import com.smarttoolfactory.data.mapper.DTOtoEntityMapper
import com.smarttoolfactory.data.model.PostDTO
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.data.source.LocalPostDataSourceCoroutines
import com.smarttoolfactory.data.source.RemotePostDataSourceCoroutines
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
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
internal class PostRepositoryCoroutinesTest {

    private lateinit var repository: PostRepositoryCoroutinesImpl

    private val localPostDataSource: LocalPostDataSourceCoroutines = mockk()
    private val remotePostDataSource: RemotePostDataSourceCoroutines = mockk()
    private val mapper: DTOtoEntityMapper = mockk()

    companion object {
        val postDTOList =
            convertFromJsonToListOf<PostDTO>(getResourceAsText(RESPONSE_JSON_PATH))!!

        val postEntityList =
            convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
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
            val actual = postEntityList
            coEvery { remotePostDataSource.getPostDTOs() } returns postDTOList
            every { mapper.map(postDTOList) } returns postEntityList

            // WHEN
            val expected = repository.fetchEntitiesFromRemote()

            // THEN
            Truth.assertThat(expected).isEqualTo(actual)
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
        coEvery { localPostDataSource.getPostEntities() } returns postEntityList

        // WHEN
        val actual = repository.getPostEntitiesFromLocal()

        // THEN
        Truth.assertThat(actual)
            .containsExactlyElementsIn(postEntityList)
        coVerify(exactly = 1) { localPostDataSource.getPostEntities() }
    }

    @Test
    fun `given entities, should save entities`() = runBlockingTest {

        // GIVEN
        val idList = postEntityList.map {
            it.id.toLong()
        }

        coEvery {
            localPostDataSource.saveEntities(postEntityList)
        } returns idList

        // WHEN
        val result = localPostDataSource.saveEntities(postEntityList)

        // THEN
        Truth.assertThat(result).containsExactlyElementsIn(idList)
        coVerify(exactly = 1) { localPostDataSource.saveEntities(postEntityList) }
    }

    @Test
    fun `given no error should delete entities`() = runBlockingTest {

        // GIVEN
        coEvery { localPostDataSource.deletePostEntities() } just runs

        // WHEN
        repository.deletePostEntities()

        // THEN
        coVerify(exactly = 1) {
            localPostDataSource.deletePostEntities()
        }
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
