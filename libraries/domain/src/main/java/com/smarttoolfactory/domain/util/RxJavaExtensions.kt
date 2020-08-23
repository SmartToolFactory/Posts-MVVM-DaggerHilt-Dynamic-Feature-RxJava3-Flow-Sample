package com.smarttoolfactory.domain.util

import com.smarttoolfactory.domain.viewstate.Status
import com.smarttoolfactory.domain.viewstate.ViewState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *
 * Extension Functions for RxJava2
 * @author Smart Tool Factory
 *
 */
fun <T> Observable<T>.listen(
    scheduleSubscribe: Scheduler,
    schedulerObserve: Scheduler
): Observable<T> {
    return subscribeOn(scheduleSubscribe)
        .observeOn(schedulerObserve)
}

fun <T> Observable<T>.subscribeOnIoObserveOnComputation(): Observable<T> {
    return listen(Schedulers.io(), Schedulers.computation())
}

fun <T> Single<T>.listen(
    scheduleSubscribe: Scheduler,
    schedulerObserve: Scheduler
): Single<T> {
    return subscribeOn(scheduleSubscribe)
        .observeOn(schedulerObserve)
}

fun <T> Single<T>.subscribeOnIoObserveOnComputation(): Single<T> {
    return listen(Schedulers.io(), Schedulers.computation())
}

fun <T> Observable<T>.observeResultOnIO(
    onNext: (T) -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): Disposable {
    return subscribeOnIoObserveOnComputation()
        .subscribe(
            {
                onNext(it)
            },
            { throwable ->
                onError?.apply {
                    onError(throwable)
                }
            },
            {
                onComplete?.apply {
                    onComplete()
                }
            }
        )
}

fun <T> Observable<T>.convertToObservableViewState(): Observable<ViewState<T>> {
    return this
        .map { data ->
            ViewState(status = Status.SUCCESS, data = data)
        }
        .onErrorResumeNext { throwable: Throwable ->
            Observable.just(ViewState(status = Status.ERROR, error = throwable))
        }
        .startWith(Observable.just(ViewState(status = Status.LOADING)))
}

fun <T> Single<T>.convertFromSingleToObservableViewState(): Observable<ViewState<T>> {
    return this
        .toObservable()
        .convertToObservableViewState()
}

fun <T> Single<T>.convertToSingleViewState(): Single<ViewState<T>> {
    return this
        .map { data ->
            ViewState(status = Status.SUCCESS, data = data)
        }
        .onErrorResumeNext { throwable: Throwable ->
            Single.just(ViewState(status = Status.ERROR, error = throwable))
        }
}

fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}
