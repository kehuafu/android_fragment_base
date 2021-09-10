package com.example.demo.test.bean

import com.kehuafu.base.core.redux.IState


data class Token(
    val init: Boolean = true, //是否需要初始化用户信息
    val token: String = "",
    val uid: String
) : IState {

    fun isLogin(): Boolean {
        return token.isNotBlank()
    }
}