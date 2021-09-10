package com.kehuafu.base.core.redux

sealed class Async<out T>(val complete: Boolean, val shouldLoad: Boolean) : Action {

    open operator fun invoke(): T? = null

    object Uninitialized : Async<Nothing>(complete = false, shouldLoad = true)

    class Loading<out T> : Async<T>(complete = false, shouldLoad = false)

    data class Succeed<out T>(val value: T) : Async<T>(complete = true, shouldLoad = false) {

        override fun invoke(): T = value
    }

    data class Failed<out T>(val error: Throwable) : Async<T>(complete = true, shouldLoad = true)
}