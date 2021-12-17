package com.example.demo.chat.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.demo.databinding.LayItemChatMsgBinding
import com.example.demo.databinding.LayItemMessageBinding
import com.example.demo.fragment.message.bean.Message
import com.kehuafu.base.core.container.base.adapter.BaseListAdapter
import com.kehuafu.base.core.ktx.viewBindings

/**
 * 测试列表适配器
 * 1.实体->T：Token
 * 2.视图->VB：LayItemTestBinding
 * 3.初始化->①设置状态监听，②属性委托对应的VB
 * 4.UI状态绑定->当数据的state变化时，对应的UI跟随状态改变
 */
class ChatListAdapter : BaseListAdapter<Message, LayItemChatMsgBinding>() {

    /**
     * 初始化适配器
     */
    override fun init(parent: ViewGroup): LayItemChatMsgBinding {
        setStateListener(this)
        val viewBinding by parent.viewBindings<LayItemChatMsgBinding>(parent)
        return viewBinding
    }

    /**
     * UI状态绑定
     */
    @SuppressLint("SetTextI18n")
    override fun setState(item: Message, viewBinding: LayItemChatMsgBinding) {
        viewBinding.leftMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item)
        }
        viewBinding.rightMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item)
        }
        viewBinding.leftMsgText.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item)
        }
        viewBinding.rightMsgText.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item)
        }
        if (item.messageSender) {
            viewBinding.layoutLeftText.visibility = View.GONE
            viewBinding.layoutRightText.visibility = View.VISIBLE
            viewBinding.rightMsgText.text = item.messageContent
        } else {
            viewBinding.layoutLeftText.visibility = View.VISIBLE
            viewBinding.layoutRightText.visibility = View.GONE
            viewBinding.leftMsgText.text = item.messageContent
        }
    }
}