package com.kehuafu.base.core.container.base.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.text.FieldPosition

/**
 *  Created by light
 *  on 2021/09/06
 *
 *  desc: 列表适配器
 */
abstract class BaseListMultipleAdapter<T> :
    BaseRecyclerViewAdapterV3<ViewBinding, T, BaseListMultipleAdapter.VH<T>>(), StateListenerV3<T> {

    private var mStateListener: StateListenerV3<T>? = null

    protected fun setStateListener(stateListener: StateListenerV3<T>) {
        this.mStateListener = stateListener
    }

    override fun onCreateVH(parent: ViewGroup, viewType: Int): VH<T> {
        return VH(init(parent, viewType), mStateListener)
    }

    class VH<T>(
        override val viewBinding: ViewBinding,
        private var mStateListener: StateListenerV3<T>? = null
    ) : BaseRecyclerViewAdapterV3.BaseViewHolder<T>(viewBinding) {
        override fun setState(item: T) {
            super.setState(item)
            mStateListener?.onStateListener(item, viewBinding, adapterPosition)
        }
    }

    abstract fun init(parent: ViewGroup, viewType: Int): ViewBinding

    abstract fun setState(item: T, viewBinding: ViewBinding, position: Int)

    override fun onStateListener(item: T, viewBinding: ViewBinding, position: Int) {
        this.setState(item, viewBinding, position)
    }
}

interface StateListenerV3<T> {
    fun onStateListener(item: T, viewBinding: ViewBinding, position: Int)
}