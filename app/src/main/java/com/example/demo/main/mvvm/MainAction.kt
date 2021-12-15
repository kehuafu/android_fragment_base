package com.example.demo.main.mvvm

import com.kehuafu.base.core.redux.Action


/**
 *
 * on 2021-09-08
 *
 * desc:
 */
sealed class MainAction : Action {
    class Success(val token: String) : MainAction()
}