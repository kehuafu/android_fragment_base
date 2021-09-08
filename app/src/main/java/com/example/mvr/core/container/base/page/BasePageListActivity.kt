package com.example.mvr.core.container.base.page

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.example.mvr.core.container.base.BaseActivity
import com.example.mvr.core.mvvm.BasePageListViewModel
import com.example.mvr.core.mvvm.page.PageListState
import com.example.mvr.core.container.widget.refresh.RecyclerRefreshLayout

/**
 * Created by light
 *
 * on 2021-09-06
 *
 * desc:
 *
 */
abstract class BasePageListActivity<T, VM : BasePageListViewModel<T>, VB : ViewBinding> :
    BaseActivity<VB, VM, PageListState<T>>(), RecyclerRefreshLayout.SuperRefreshLayoutListener {

    protected val refreshLayout: RecyclerRefreshLayout? by lazy {
        val identifierId = resources.getIdentifier("refresh", "id", applicationInfo.packageName)
        findViewById<RecyclerRefreshLayout?>(identifierId)
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