package com.kehuafu.base.core.mvvm.page

import com.kehuafu.base.core.redux.Action


/**
 *
 * on 2021-09-06
 *
 * desc:
 */
sealed class PageListAction<T> : Action {
    abstract val data: List<T>
    abstract val hasMore: Boolean

    class Refresh<T>(override val data: List<T>, override val hasMore: Boolean) :
        PageListAction<T>()

    class Next<T>(override val data: List<T>, override val hasMore: Boolean) : PageListAction<T>()
}