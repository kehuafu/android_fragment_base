package com.example.demo.base

import com.example.demo.app.AppManager
import com.kehuafu.base.core.ktx.asyncCall
import com.kehuafu.base.core.network.error.ErrorResponse
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.redux.Middleware
import com.kehuafu.base.core.redux.Reducer
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.mvvm.BaseViewModel

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