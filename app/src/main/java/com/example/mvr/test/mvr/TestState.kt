package com.example.mvr.test.mvr

import com.example.mvr.test.bean.Token
import com.example.mvr.core.redux.IState


/**
 *
 * on 2019-11-08
 *
 * desc:
 */
data class TestState(val token: Token) : IState {

}