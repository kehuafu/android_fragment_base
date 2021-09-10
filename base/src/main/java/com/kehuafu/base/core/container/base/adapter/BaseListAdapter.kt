package com.kehuafu.base.core.container.base.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 *  Created by light
 *  on 2021/09/06
 *
 *  desc: 列表适配器
 */
abstract class BaseListAdapter<T, VB : ViewBinding> :
    BaseRecyclerViewAdapterV2<VB, T, BaseListAdapter.VH<T, VB>>(), StateListener<T, VB> {

    private var mStateListener: StateListener<T, VB>? = null

    protected fun setStateListener(stateListener: StateListener<T, VB>) {
        this.mStateListener = stateListener
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): VH<T, VB> {
        return VH(init(parent), mStateListener)
    }

    class VH<T, VB : ViewBinding>(
        override val viewBinding: VB,
        private var mStateListener: StateListener<T, VB>? = null
    ) : BaseViewHolder<T, VB>(viewBinding) {
        override fun setState(item: T) {
            super.setState(item)
            mStateListener?.onStateListener(item, viewBinding)
        }
    }

    abstract fun init(parent: ViewGroup): VB

    abstract fun setState(item: T, viewBinding: VB)

    override fun onStateListener(item: T, viewBinding: VB) {
        this.setState(item, viewBinding)
    }
}

interface StateListener<T, VB> {
    fun onStateListener(item: T, viewBinding: VB)
}