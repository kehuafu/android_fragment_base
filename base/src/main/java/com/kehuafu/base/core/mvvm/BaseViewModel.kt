@file:Suppress("BlockingMethodInNonBlockingContext")

package com.kehuafu.base.core.mvvm

import androidx.lifecycle.*
import com.kehuafu.base.core.redux.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.reflect.KProperty1

abstract class BaseViewModel<State : IState>(
    initialState: State,
    reducers: List<Reducer<State>> = listOf(),
    middleware: List<Middleware<State>> = listOf()
) : ViewModel(), IStore<State> {

    private var currentState: State = initialState

    private val currentReducer: Reducer<State> = combineReducers(reducers)

    private val _dispatchers: List<NextDispatcher> = createDispatchers(
        middleware,
        createReducerAndNotify()
    )

    private val _changeController = MutableLiveData<State>().apply {
        // value = currentState
        //Log.e("tag", "baseViewModel LiveData Init")
    }

    private val disposables = CompositeDisposable()

    private fun createDispatchers(
        middleware: List<Middleware<State>>,
        dispatcher: NextDispatcher
    ): List<NextDispatcher> {

        val dispatchers: MutableList<NextDispatcher> = mutableListOf(dispatcher)

        middleware.reversed()
            .map { nextMiddleware: Middleware<State> ->
                val next = dispatchers.last()
                dispatchers.add { action: Action ->
                    nextMiddleware.invoke(
                        this,
                        action,
                        next
                    )
                }
            }

        return dispatchers.reversed()
    }

    private fun createReducerAndNotify(): NextDispatcher {
        return { action: Action ->
            val state = currentReducer.invoke(currentState, action)
            currentState = state
            //Log.e("TAG", "createReducerAndNotify")
            _changeController.postValue(currentState)
        }
    }

    private fun combineReducers(reducers: List<Reducer<State>>): Reducer<State> {
        return { state: State, action: Action ->
            reducers.fold(initial = state, operation = { acc: State, func: Reducer<State> ->
                return@fold func.invoke(
                    acc,
                    action
                )
            })
        }
    }

    override fun dispatch(action: Action) {
        _dispatchers[0].invoke(action)
    }

    override fun setState(stateSetter: StateSetter<State>) {
        val newState = stateSetter.invoke(currentState)
        currentState = newState
        _changeController.postValue(currentState)
    }

    override fun getState(): State = currentState

    override fun withState(getter: StateGetter<State>) {
        currentState.apply(getter)
    }

    override fun subscribe(lifecycleOwner: LifecycleOwner, subscriber: Subscriber<State>) {
        _changeController
            //.distinctUntilChanged()
            .observe(lifecycleOwner, Observer(subscriber))
    }

    override fun <T> asyncSubscribe(
        lifecycleOwner: LifecycleOwner,
        asyncProp: KProperty1<State, Async<T>>,
        onProcess: OnProcess?,
        onFail: OnFail?,
        onSuccess: OnSuccess<T>?
    ) {
        _changeController.map { state: State -> Tuple1(a = asyncProp.get(receiver = state)) }
            //.distinctUntilChanged()
            .observe(lifecycleOwner, Observer { tuple1: Tuple1<Async<T>> ->
                when (tuple1.a) {
                    is Async.Uninitialized -> {
                        onProcess?.invoke()
                    }
                    is Async.Loading ->
                        onProcess?.invoke()
                    is Async.Failed ->
                        onFail?.invoke(tuple1.a.error)
                    is Async.Succeed ->
                        onSuccess?.invoke(tuple1.a.value)
                }
            })
    }

    fun <T> execute(asyncSetter: AsyncSetter<State, T>, block: suspend () -> T) {
        // Log.e("TAG", "execute")
        viewModelScope.launch {
            setState { asyncSetter(Async.Loading()) }
            try {
                val t = block.invoke()
                setState { asyncSetter(Async.Succeed(t)) }
            } catch (e: Exception) {
                e.printStackTrace()
                setState { asyncSetter(Async.Failed(error = e)) }
                when (e) {
                    is HttpException -> {
                    }
                    else -> {
                    }
                }
            } finally {
                //  Log.e("TAG", "finally")
            }
        }
    }

    override fun <T> Single<T>.execute(asyncSetter: AsyncSetter<State, T>): Disposable {
        return toObservable()
            .execute(asyncSetter)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> Observable<T>.execute(asyncSetter: AsyncSetter<State, T>): Disposable {
        return doOnSubscribe { setState { asyncSetter(Async.Loading()) } }
            .subscribe({ data: T ->
                setState { asyncSetter(Async.Succeed(value = data) as Async<T>) }
            }, { error: Throwable ->
                setState { asyncSetter(Async.Failed(error = error)) }
            })
    }

    override fun Disposable.disposeOnClear(): Disposable {
        disposables.add(this)
        return this
    }

    override fun onCleared() {
        disposables.clear()
    }
}