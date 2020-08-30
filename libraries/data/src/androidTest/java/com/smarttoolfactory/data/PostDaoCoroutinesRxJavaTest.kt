package com.smarttoolfactory.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import com.smarttoolfactory.data.db.PostDaoRxJava3
import com.smarttoolfactory.data.db.PostDatabase
import com.smarttoolfactory.data.model.PostEntity
import com.smarttoolfactory.test_utils.RESPONSE_JSON_PATH
import com.smarttoolfactory.test_utils.util.convertFromJsonToListOf
import com.smarttoolfactory.test_utils.util.getResourceAsText
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PostDaoCoroutinesRxJavaTest {

    companion object {
        val postEntityList =
            convertFromJsonToListOf<PostEntity>(getResourceAsText(RESPONSE_JSON_PATH))!!
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PostDatabase

    /**
     * This is the SUT
     */
    private lateinit var postDao: PostDaoRxJava3

    @Test
    fun shouldInsertSinglePost() {

        // GIVEN
        val initialCount = postDao.getPostCount().blockingGet()

        // WHEN
        val testObserver = postDao.insert(postEntityList.first()).test()

        // THEN
        testObserver.assertComplete()
            .assertNoErrors()

        val postCount = postDao.getPostCount().blockingGet()
        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(postCount).isEqualTo(1)

        testObserver.dispose()
    }

    @Test
    fun shouldInsertMultiplePosts() {

        // GIVEN
        val initialCount = postDao.getPostCount().blockingGet()

        // WHEN
        val testObserver = postDao.insert(postEntityList).test()

        // THEN
        testObserver.assertComplete()
            .assertNoErrors()

        val postCount = postDao.getPostCount().blockingGet()
        Truth.assertThat(initialCount).isEqualTo(0)
        Truth.assertThat(postCount).isEqualTo(postEntityList.size)

        testObserver.dispose()
    }

    /*
        Get Post List Single
     */
    @Test
    fun givenDBEmptyShouldReturnEmptyListWithSingle() {

        // GIVEN
        val postCount = postDao.getPostCount().blockingGet()

        // WHEN
        val postEntityList = postDao.getPostsSingle().blockingGet()

        // THEN
        Truth.assertThat(postCount).isEqualTo(0)
        Truth.assertThat(postEntityList).hasSize(0)
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrectListWithSingle() {

        // GIVEN
        val expected = postEntityList[0]
        postDao.insert(expected).blockingAwait()

        // WHEN
        val postEntityList = postDao.getPostsSingle().blockingGet()

        // THEN
        val actual = postEntityList[0]
        Truth.assertThat(actual).isEqualTo(expected)
    }

    /*
        Get Post List Maybe
     */
    @Test
    fun givenDBEmptyShouldReturnEmptyListWithMaybe() {

        // GIVEN
        val expected = listOf<PostEntity>()

        // WHEN
        val testObserver = postDao.getPostsMaybe().test()

        // THEN
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                it.size == expected.size
            }
            .dispose()
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrectListWithMaybe() {

        // GIVEN
        val expected = postEntityList[0]
        postDao.insert(expected).blockingAwait()

        // WHEN
        val testObserver = postDao.getPostsMaybe().test()

        // THEN
        testObserver
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                it.size == 1
            }
            .dispose()
    }

    /*
        Get Post List Observable
     */

    @Test
    fun givenDBEmptyShouldReturnEmptyListWithObservable() {

        // GIVEN
        val expected = listOf<PostEntity>()

        // 🔥 await() not work with Observable
//        // WHEN
//        val testObserver = postDao.getPosts().test()
//
//        // THEN
//        testObserver.await()
//
//        testObserver
//            .assertNoErrors()
//            .assertComplete()
//            .assertValue {
//                it.size == expected.size
//            }
//            .dispose()

        // WHEN
        val posts = postDao.getPosts().blockingFirst()

        // THEN
        Truth.assertThat(posts.size).isEqualTo(expected.size)
    }

    @Test
    fun giveDBPopulatedShouldReturnCorrectListWithObservable() {

        // GIVEN
        val expected = postEntityList
        postDao.insert(expected).blockingAwait()

        // 🔥 await() not work with Observable
//         // WHEN
//        val testObserver = postDao.getPosts().test()
//
//         // THEN
//        testObserver.await()
//
//        testObserver
//            .assertNoErrors()
//            .assertComplete()
//            .assertValue {
//                it.size == expected.size
//            }
//            .dispose()

        // WHEN
        val posts = postDao.getPosts().blockingFirst()

        // THEN
        Truth.assertThat(posts.size).isEqualTo(postEntityList.size)
        Truth.assertThat(posts).containsExactlyElementsIn(postEntityList)
    }

    /*
        Delete Suspend
     */

    @Test
    fun givenEveryPostDeletedShouldReturnEmptyList() {

        // GIVEN
        postDao.insert(postEntityList).blockingAwait()
        val initialPostCount = postDao.getPostCount().blockingGet()

        // WHEN
        postDao.deleteAll().blockingAwait()

        // THEN
        val postCount = postDao.getPostCount().blockingGet()
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

        postDao = database.postDaoRxJava()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }
}
