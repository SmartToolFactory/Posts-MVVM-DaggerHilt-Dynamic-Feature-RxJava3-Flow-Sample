package com.smarttoolfactory.test_utils.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.coroutineContext

class FlowTestObserver<T>(
    private val coroutineScope: CoroutineScope,
    private val flow: Flow<T>,
    private val waitForDelay: Boolean = false
) {
    private val testValues = mutableListOf<T>()
    private var error: Throwable? = null

    private var isInitialized = false

    private var isCompleted = false

    private lateinit var job: Job

    private suspend fun initializeAndJoin() {
        job = createJob(coroutineScope)

        // Wait this job after end of possible delays
//        job.join()
    }

    private suspend fun initialize() {

        if (!isInitialized) {
            isInitialized = true

            if (waitForDelay) {
                try {
                    withTimeout(Long.MAX_VALUE) {
                        job = createJob(this)
                    }
                } catch (e: Exception) {
                    isCompleted = false
                }
            } else {
                initializeAndJoin()
            }
        }
    }

    private fun createJob(scope: CoroutineScope): Job {

        val job = flow
            .onStart {}
            .onCompletion {
                isCompleted = true
            }
            .catch { throwable ->
                error = throwable
            }
            .onEach {
                testValues.add(it)
            }
            .launchIn(scope)
        return job
    }

    suspend fun assertNoValues(): FlowTestObserver<T> {

        initialize()

        if (testValues.isNotEmpty()) throw AssertionError(
            "Assertion error! Actual size ${testValues.size}"
        )
        return this
    }

    suspend fun assertValueCount(count: Int): FlowTestObserver<T> {

        initialize()

        if (count < 0) throw AssertionError(
            "Assertion error! Value count cannot be smaller than zero"
        )
        if (count != testValues.size) throw AssertionError(
            "Assertion error! Expected $count while actual ${testValues.size}"
        )
        return this
    }

    suspend fun assertValues(vararg values: T): FlowTestObserver<T> {

        initialize()

        if (!testValues.containsAll(values.asList()))
            throw AssertionError("Assertion error! At least one value does not match")
        return this
    }

    suspend fun assertValues(predicate: (List<T>) -> Boolean): FlowTestObserver<T> {

        initialize()

        if (!predicate(testValues))
            throw AssertionError("Assertion error! At least one value does not match")
        return this
    }

    suspend fun assertError(throwable: Throwable): FlowTestObserver<T> {

        initialize()

        val errorNotNull = exceptionNotNull()

        if (!(
            errorNotNull::class.java == throwable::class.java &&
                errorNotNull.message == throwable.message
            )
        )
            throw AssertionError("Assertion Error! throwable: $throwable does not match $errorNotNull")
        return this
    }

    suspend fun assertError(errorClass: Class<Throwable>): FlowTestObserver<T> {

        initialize()

        val errorNotNull = exceptionNotNull()

        if (errorNotNull::class.java != errorClass)
            throw AssertionError("Assertion Error! errorClass $errorClass does not match ${errorNotNull::class.java}")
        return this
    }

    suspend fun assertError(predicate: (Throwable) -> Boolean): FlowTestObserver<T> {

        initialize()

        val errorNotNull = exceptionNotNull()

        if (!predicate(errorNotNull))
            throw AssertionError("Assertion Error! Exception for $errorNotNull")
        return this
    }

    suspend fun assertNoError(): FlowTestObserver<T> {

        initialize()

        if (error != null)
            throw AssertionError("Assertion Error! Exception occurred $error")

        return this
    }

    suspend fun assertNull(): FlowTestObserver<T> {

        initialize()

        testValues.forEach {
            if (it != null) throw AssertionError("Assertion Error! There are more than one item that is not null")
        }

        return this
    }

    suspend fun assertComplete(): FlowTestObserver<T> {

        initialize()

        if (!isCompleted) throw AssertionError("Assertion Error! Job is not completed yet!")
        return this
    }

    suspend fun assertNotComplete(): FlowTestObserver<T> {

        initialize()

        if (isCompleted) throw AssertionError("Assertion Error! Job is completed!")
        return this
    }

    suspend fun values(predicate: (List<T>) -> Unit): FlowTestObserver<T> {
        predicate(testValues)
        return this
    }

    suspend fun values(): List<T> {

        initialize()

        return testValues
    }

    private fun exceptionNotNull(): Throwable {

        if (error == null)
            throw AssertionError("There is no exception")

        return error!!
    }

    fun dispose() {
        job.cancel()
    }
}

/**
 * Creates a RxJava2 style test observer that uses `onStart`, `onEach`, `onCompletion`
 *
 * * Set waitForDelay true for testing delay.
 *
 * ###  Note: waiting for delay with a channel that sends values throw TimeoutCancellationException, don't use timeout with channel
 * TODO Fix channel issue
 */
suspend fun <T> Flow<T>.test(
    scope: CoroutineScope,
    waitForDelay: Boolean = false
): FlowTestObserver<T> {

    return FlowTestObserver(scope, this@test, waitForDelay)
}

/**
 * Test function that awaits with time out until each delay method is run and then since
 * it takes a predicate that runs after a timeout.
 */
suspend fun <T> Flow<T>.testAfterDelay(
    scope: CoroutineScope,
    predicate: suspend FlowTestObserver<T>.() -> Unit

): Job {
    return scope.launch(coroutineContext) {
        FlowTestObserver(this, this@testAfterDelay, true).predicate()
    }
}
