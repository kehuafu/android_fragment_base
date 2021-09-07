package com.example.mvr.core.redux

import androidx.lifecycle.LifecycleOwner
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import kotlin.reflect.KProperty1

interface IStore<State : IState> {

    fun dispatch(action: Action)

    fun setState(stateSetter: StateSetter<State>)

    fun getState(): State

    fun withState(getter: StateGetter<State>)

    fun subscribe(
        lifecycleOwner: LifecycleOwner,
        subscriber: Subscriber<State>
    )

    fun <T> asyncSubscribe(
        lifecycleOwner: LifecycleOwner,
        asyncProp: KProperty1<State, Async<T>>,
        onProcess: OnProcess? = null,
        onFail: OnFail? = null,
        onSuccess: OnSuccess<T>? = null
    )

    fun <T> Single<T>.execute(asyncSetter: AsyncSetter<State, T>): Disposable

    fun <T> Observable<T>.execute(asyncSetter: AsyncSetter<State, T>): Disposable

    fun Disposable.disposeOnClear(): Disposable
}