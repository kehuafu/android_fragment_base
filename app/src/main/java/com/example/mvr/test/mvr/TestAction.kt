package com.example.mvr.test.mvr

import com.example.mvr.test.bean.Token
import com.example.mvr.core.redux.Action


/**
 *
 * on 2019-11-08
 *
 * desc:
 */
sealed class TestAction : Action {
    class Success(val token: Token) : TestAction()
}