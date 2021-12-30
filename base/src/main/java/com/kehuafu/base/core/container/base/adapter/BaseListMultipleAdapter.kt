package com.kehuafu.base.core.container.base.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 *  Created by light
 *  on 2021/09/06
 *
 *  desc: 列表适配器
 */
abstract class BaseListMultipleAdapter<T> :
    BaseRecyclerViewAdapterV4<T, BaseRecyclerViewAdapterV4.BaseViewHolder<T>>() {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        mOnItemClickListener?.let { setOnItemClickListener(it) }
        return bindVH(parent, viewType)
    }

    abstract fun bindVH(parent: ViewGroup, viewType: Int): BaseViewHolder<T>
}