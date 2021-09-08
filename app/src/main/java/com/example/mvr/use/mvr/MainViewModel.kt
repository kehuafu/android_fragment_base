package com.example.mvr.use.mvr

import com.example.mvr.test.bean.Token
import com.example.mvr.core.mvvm.BaseRequestViewModel
import com.example.mvr.core.redux.Reducer
import com.example.mvr.core.container.widget.toast.showToast


class MainViewModel : BaseRequestViewModel<MainState>(
    initialState = MainState(token = Token(init = false, token = "", uid = "")),
    reducers = listOf(reducer())
) {
    companion object {
        private fun reducer(): Reducer<MainState> {
            return { state, action ->
                when (action) {
                    is MainAction.Success -> {
                        state.copy(token = action.token)
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun main(uid: String, token: String) {
        httpAsyncCall({
            showToast(it.errorCode.toString())
        }) {
            val test = Token(
                token = token,
                uid = uid,
                init = false
            )
            //请求后台数据
//            val token = AppManager.apiService.test(testBody = TestBody(uid = uid, token = token))
            dispatch(MainAction.Success(token = test))
        }
    }
}