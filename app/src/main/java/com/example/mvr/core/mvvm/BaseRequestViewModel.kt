package com.example.mvr.core.mvvm

import com.example.mvr.app.AppManager
import com.example.mvr.core.ktx.asyncCall
import com.example.mvr.core.net.error.ErrorResponse
import com.example.mvr.core.redux.IState
import com.example.mvr.core.redux.Middleware
import com.example.mvr.core.redux.Reducer
import com.example.mvr.core.container.widget.toast.showToast

abstract class BaseRequestViewModel<State : IState>(
    initialState: State,
    reducers: List<Reducer<State>> = listOf(),
    middleware: List<Middleware<State>> = listOf(),
) : BaseViewModel<State>(initialState, reducers, middleware) {

    protected val apiService by lazy {
        AppManager.apiService
    }

    protected fun httpAsyncCall(
        failedBlock: ((ErrorResponse) -> Unit)? = null,
        execute: suspend () -> Unit
    ) {
        asyncCall(failedBlock = {
            if (it.unauthorized()) {
                showToast(it.errorMsg)
                //TODO:something()
            } else if (it.appVersionForbidden()) {
                //TODO:appVersionUpgrade()
            }
            failedBlock?.invoke(it)
        }) {
            execute.invoke()
        }
    }
}