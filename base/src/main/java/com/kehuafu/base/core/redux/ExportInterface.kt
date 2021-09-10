package com.kehuafu.base.core.redux

import com.kehuafu.base.core.mvvm.BaseViewModel

interface Action

typealias Reducer<State> = (state: State, action: Action) -> State

typealias NextDispatcher = (action: Action) -> Unit

typealias Middleware<State> = (store: BaseViewModel<State>, action: Action, dispatcher: NextDispatcher) -> Unit

typealias StateGetter<State> = (state: State) -> Unit

typealias StateSetter<State> = State.() -> State

typealias AsyncSetter<State, T> = State.(Async<T>) -> State

typealias Subscriber<State> = (state: State) -> Unit

typealias OnProcess = () -> Unit

typealias OnFail = (error: Throwable) -> Unit

typealias OnSuccess<T> = (data: T) -> Unit
