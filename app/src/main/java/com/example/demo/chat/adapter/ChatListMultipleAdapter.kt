package com.example.demo.chat.adapter

import android.view.ViewGroup
import com.example.demo.chat.bean.Message
import com.kehuafu.base.core.container.base.adapter.BaseListMultipleAdapter


/**
 * 测试列表适配器
 * 1.实体->T：Token
 * 2.视图->VB：LayItemTestBinding
 * 3.初始化->①设置状态监听，②属性委托对应的VB
 * 4.UI状态绑定->当数据的state变化时，对应的UI跟随状态改变
 */
class ChatListMultipleAdapter : BaseListMultipleAdapter<Message>() {

    private val mChatMsgAdapterTypeDelegate by lazy {
        ChatMsgAdapterTypeDelegate()
    }

    override fun getItemViewType(position: Int): Int {
        return if (mItems.isEmpty()) {
            super.getItemViewType(position)
        } else {
            mChatMsgAdapterTypeDelegate.getItemViewType(mItems[position])
        }
    }

    override fun bindVH(parent: ViewGroup, viewType: Int): BaseViewHolder<Message> {
        return mChatMsgAdapterTypeDelegate.onCreateViewHolder(parent, viewType)
    }
}