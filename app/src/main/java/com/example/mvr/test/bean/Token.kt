package com.example.mvr.test.bean

import com.example.mvr.core.redux.IState


data class Token(
    val init: Boolean = true, //是否需要初始化用户信息
    val token: String = "",
    val uid: String
) : IState {

    fun isLogin(): Boolean {
        return token.isNotBlank()
    }
}