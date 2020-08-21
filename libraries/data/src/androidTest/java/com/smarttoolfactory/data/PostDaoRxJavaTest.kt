package com.smarttoolfactory.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import com.smarttoolfactory.data.db.PostDaoRxJava
import com.smarttoolfactory.data.db.PostDatabase
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToObjectList
import com.smarttoolfactory.test_utils.util.getResourceAsText
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostDaoRxJavaTest {

    companion object {
        val postEntityList =
            convertFromJsonToObjectList<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PostDatabase

    /**
     * This is the SUT
     */
    private lateinit var postDao: PostDaoRxJava


    @Test
    fun shouldInsertSinglePost() = runBlockingTest {

        // GIVEN
        val initialCount = postDao.getPostCount()

        // WHEN
        val insertedId = postDao.insert(PostDaoCoroutinesFlowTest.postEntityList.first())

        // THEN
        val postCount = postDao.getPostCount()


        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(insertedId).isEqualTo(1)
        Truth.assertThat(postCount).isEqualTo(1)
    }

    @Test
    fun shouldInsertMultiplePosts() = runBlockingTest {

        // GIVEN
        val initialCount = postDao.getPostCount().blockingGet()

        // WHEN
        val ids = postDao.insert(PostDaoCoroutinesFlowTest.postEntityList)

        // THEN
        val postCount = postDao.getPostCount()
        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(postCount).isEqualTo(PostDaoCoroutinesFlowTest.postEntityList.size)
    }

    /*
        Get Post List Suspend
     */

    @Test
    fun givenDBEmptyShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        val postCount = postDao.getPostCount().blockingGet()

        // WHEN
        val postEntityList = postDao.getPostsSingle().blockingGet()

        // THEN
        Truth.assertThat(postCount).isEqualTo(0)
        Truth.assertThat(postEntityList).hasSize(0)
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrecList() = runBlockingTest {

        // GIVEN
        val expected = PostDaoCoroutinesFlowTest.postEntityList[0]
        postDao.insert(expected)

        // WHEN
        val postEntityList = postDao.getPostsSingle().blockingGet()

        // THEN
        val actual = postEntityList[0]
        Truth.assertThat(actual).isEqualTo(expected)
    }


    /*
        Delete Suspend
     */

    @Test
    fun givenEveryPostDeletedShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        postDao.insert(PostDaoCoroutinesFlowTest.postEntityList)
        val initialPostCount = postDao.getPostCount().blockingGet()

        // WHEN
        postDao.deleteAll().blockingAwait()

        // THEN
        val postCount = postDao.getPostCount()
        Truth.assertThat(initialPostCount).isEqualTo(PostDaoCoroutinesFlowTest.postEntityList.size)
        Truth.assertThat(postCount).isEqualTo(0)
    }

    @Before
    fun setUp() {

        // using an in-memory database because the information stored here disappears after test
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), PostDatabase::class.java
        )
            // allowing main thread queries, just for testing
//            .allowMainThreadQueries()
            .build()

        postDao = database.postDaoRxJava()

    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }
}