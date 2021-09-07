package com.example.mvr.core.mvvm

import android.util.Log
import androidx.annotation.MainThread
import com.example.mvr.core.mvvm.page.PageListAction
import com.example.mvr.core.mvvm.page.PageListState
import com.example.mvr.core.redux.Reducer

/**
 *
 * on 2021-09-06
 *
 * desc:
 */
abstract class BasePageListViewModel<T> : BaseRequestViewModel<PageListState<T>>(
    initialState = PageListState<T>(data = emptyList(), refresh = false, hasMore = false),
    reducers = listOf(reducer<T>())
) {
    companion object {
        private val TAG = BasePageListViewModel::class.java.simpleName

        private const val DEFAULT_PAGE_SIZE = 20
        const val DEFAULT_PAGE_INDEX = 1

        @Suppress("UNCHECKED_CAST")
        private fun <T> reducer(): Reducer<PageListState<T>> {
            return { state, action ->
                when (action) {
                    is PageListAction.Refresh<*> -> {
                        Log.e("Refresh", "")
                        state.copy(
                            data = (action.data as List<T>),
                            hasMore = action.hasMore,
                            refresh = true
                        )
                    }
                    is PageListAction.Next<*> -> {
                        Log.e("Next", "")
                        state.copy(
                            data = (action.data as List<T>),
                            hasMore = action.hasMore,
                            refresh = false
                        )
                    }
                    else -> {
                        Log.e("else", "")
                        state
                    }
                }
            }
        }
    }

    protected var pageIndex = DEFAULT_PAGE_INDEX
    protected var hasMore = false
    protected var refresh = false
    protected var itemSize = 0

    @MainThread
    fun refreshPage() {
        pageIndex = DEFAULT_PAGE_INDEX
        refresh = true
        itemSize = 0
        action()
    }

    @MainThread
    fun loadNextPage() {
        if (!hasMore) {
            Log.e(TAG, "no have more data")
            dispatch(PageListAction.Next(data = emptyList<T>(), hasMore = hasMore))
            return
        }
        refresh = false
        pageIndex += 1
        action()
    }

    @MainThread
    protected abstract fun action()
}