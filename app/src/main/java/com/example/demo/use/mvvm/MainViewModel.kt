package com.example.demo.use.mvvm

import com.example.demo.test.bean.Token
import com.kehuafu.base.core.container.widget.toast.showToast
import com.example.demo.base.BaseRequestViewModel
import com.kehuafu.base.core.redux.Reducer


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