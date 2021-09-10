package com.example.demo.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.mvvm.page.PageListState
import com.kehuafu.base.core.container.widget.refresh.RecyclerRefreshLayout


/**
 * Created by light
 *
 * on 2021-09-06
 *
 * desc:
 *
 */
abstract class BasePageListFragment<VB : ViewBinding, T, VM : BasePageListViewModel<T>> :
    BaseFragment<VB, VM, PageListState<T>>(), RecyclerRefreshLayout.SuperRefreshLayoutListener {

    protected val refreshLayout: RecyclerRefreshLayout? by lazy {
        val identifierId = resources.getIdentifier("refresh", "id", requireActivity().packageName)
        requireView().findViewById<RecyclerRefreshLayout>(identifierId)
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        refreshLayout?.isRefreshing = true
    }

    override fun onRefreshing() {
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        refreshLayout?.setSuperRefreshLayoutListener(this)
    }

    override fun onStateChanged(state: PageListState<T>) {
        super.onStateChanged(state)
        refreshLayout?.onComplete()
    }
}