package com.example.mvr.use.mvr

import com.example.mvr.test.bean.Token
import com.example.mvr.core.redux.IState


/**
 *
 * on 2021-09-08
 *
 * desc:
 */
data class MainState(val token: Token) : IState {

}