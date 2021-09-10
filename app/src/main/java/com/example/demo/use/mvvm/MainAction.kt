package com.example.demo.use.mvvm

import com.example.demo.test.bean.Token
import com.kehuafu.base.core.redux.Action


/**
 *
 * on 2021-09-08
 *
 * desc:
 */
sealed class MainAction : Action {
    class Success(val token: Token) : MainAction()
}