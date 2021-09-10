package com.example.demo.test.mvvm

import com.example.demo.test.bean.Token
import com.kehuafu.base.core.redux.Action


/**
 *
 * on 2019-11-08
 *
 * desc:
 */
sealed class TestAction : Action {
    class Success(val token: Token) : TestAction()
}