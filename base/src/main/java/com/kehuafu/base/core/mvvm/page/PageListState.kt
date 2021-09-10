package com.kehuafu.base.core.mvvm.page

import com.kehuafu.base.core.redux.IState


/**
 *
 * on 2021-09-06
 *
 * desc:
 */
data class PageListState<T>(val data: List<T>, val refresh: Boolean, val hasMore: Boolean) : IState