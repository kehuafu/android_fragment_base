package com.example.mvr.test.mvr

import com.example.mvr.app.AppManager
import com.example.mvr.test.bean.Token
import com.example.mvr.core.mvvm.BaseRequestViewModel
import com.example.mvr.core.redux.Reducer
import com.example.mvr.core.container.widget.toast.showToast
import com.example.mvr.test.body.TestBody

class TestViewModel : BaseRequestViewModel<TestState>(
    initialState = TestState(token = Token(init = false, token = "", uid = "")),
    reducers = listOf(reducer())
) {
    companion object {
        private fun reducer(): Reducer<TestState> {
            return { state, action ->
                when (action) {
                    is TestAction.Success -> {
                        state.copy(token = action.token)
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun test(uid: String, token: String) {
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
            dispatch(TestAction.Success(token = test))
        }
    }
}