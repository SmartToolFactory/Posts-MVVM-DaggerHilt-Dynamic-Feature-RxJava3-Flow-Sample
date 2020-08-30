package com.smarttoolfactory.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.smarttoolfactory.data.db.PostDaoCoroutines
import com.smarttoolfactory.data.db.PostDatabase
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.rule.TestCoroutineRule
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostDaoCoroutinesCoroutinesFlowTest {

    companion object {
        val postEntityList =
            convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    private lateinit var database: PostDatabase

    /**
     * This is the SUT
     */
    private lateinit var postDao: PostDaoCoroutines

    /*
        Insert
     */

    @Test
    fun shouldInsertSinglePost() = runBlockingTest {

        // GIVEN
        val initialCount = postDao.getPostCount()

        // WHEN
        val insertedId = postDao.insert(postEntityList.first())

        // THEN
        val postCount = postDao.getPostCount()

        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(insertedId).isEqualTo(1)
        Truth.assertThat(postCount).isEqualTo(1)
    }

    @Test
    fun shouldInsertMultiplePosts() = runBlockingTest {

        // GIVEN
        val initialCount = postDao.getPostCount()

        // WHEN
        val ids = postDao.insert(postEntityList)

        // THEN
        val postCount = postDao.getPostCount()
        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(postCount).isEqualTo(postEntityList.size)
    }

    /*
        Get Post List Suspend
     */

    @Test
    fun givenDBEmptyShouldReturnEmptyList() = runBlockingTest {

        // GIVEN
        val postCount = postDao.getPostCount()

        // WHEN
        val postEntityList = postDao.getPostList()

        // THEN
        Truth.assertThat(postCount).isEqualTo(0)
        Truth.assertThat(postEntityList).hasSize(0)
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrectList() = runBlockingTest {

        // GIVEN
        val expected = postEntityList[0]
        postDao.insert(expected)

        // WHEN
        val postEntityList = postDao.getPostList()

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
        postDao.insert(postEntityList)
        val initialPostCount = postDao.getPostCount()

        // WHEN
        postDao.deleteAll()

        // THEN
        val postCount = postDao.getPostCount()
        Truth.assertThat(initialPostCount).isEqualTo(postEntityList.size)
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

        postDao = database.postDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }
}
