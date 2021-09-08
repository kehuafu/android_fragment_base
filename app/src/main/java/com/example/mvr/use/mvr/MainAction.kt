package com.example.mvr.use.mvr

import com.example.mvr.test.bean.Token
import com.example.mvr.core.redux.Action


/**
 *
 * on 2021-09-08
 *
 * desc:
 */
sealed class MainAction : Action {
    class Success(val token: Token) : MainAction()
}