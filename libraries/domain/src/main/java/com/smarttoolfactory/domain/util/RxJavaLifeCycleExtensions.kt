package com.smarttoolfactory.domain.util

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

fun <T> Single<T>.logLifeCycleEvents(): Single<T> {
    return this
        .doOnSubscribe {
            println("⏱ doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnSuccess {
            println("🥶 doOnSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterSuccess {
            println("😍 doAfterSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doOnTerminate {
            println("😃 doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("😑 doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("👍 doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("💀 doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("🤬 doOnError() ${it.message}")
        }
}

fun <T> Maybe<T>.logLifeCycleEvents(): Maybe<T> {

    return this
        .doOnSubscribe {
            println("⏱ doOnSubscribe() thread: ${Thread.currentThread().name}")
        }

        .doOnSuccess {
            println("🥶 doOnSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterSuccess {
            println("😍 doAfterSuccess() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("🤬 doOnError() ${it.message}")
        }
}

fun <T> Observable<T>.logLifeCycleEvents(): Observable<T> {

    return this
        .doOnSubscribe {
            println("⏱ doOnSubscribe() thread: ${Thread.currentThread().name}")
        }
        .doOnEach {
            println(
                "🎃 doOnEach() thread: ${Thread.currentThread().name}," +
                    " event: $it, val: ${it.value}"
            )
        }
        .doOnNext {
            println("🥶 doOnNext() thread: ${Thread.currentThread().name}, val: $it")
        }
        .doAfterNext {
            println("😍 doAfterNext() thread: ${Thread.currentThread().name}, val: $it")
        }

        .doOnComplete {
            println("doOnComplete() thread: ${Thread.currentThread().name}")
        }
        .doOnTerminate {
            println("doOnTerminate() thread: ${Thread.currentThread().name}")
        }
        .doAfterTerminate {
            println("doAfterTerminate() thread: ${Thread.currentThread().name}")
        }
        .doFinally {
            println("doFinally() thread: ${Thread.currentThread().name}")
        }
        .doOnDispose {
            println("doOnDispose() thread: ${Thread.currentThread().name}")
        }
        .doOnError {
            println("🤬 doOnError() ${it.message}")
        }
}
