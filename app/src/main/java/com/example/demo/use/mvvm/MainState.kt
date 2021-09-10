package com.example.demo.use.mvvm

import com.example.demo.test.bean.Token
import com.kehuafu.base.core.redux.IState


/**
 *
 * on 2021-09-08
 *
 * desc:
 */
data class MainState(val token: Token) : IState {

}