package com.example.demo.fragment.conversation.adapter

import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.databinding.LayItemConversationBinding
import com.example.demo.fragment.conversation.bean.Conversation
import com.kehuafu.base.core.container.base.adapter.BaseListAdapter
import com.kehuafu.base.core.ktx.loadImage
import com.kehuafu.base.core.ktx.viewBindings

/**
 * 测试列表适配器
 * 1.实体->T：Token
 * 2.视图->VB：LayItemTestBinding
 * 3.初始化->①设置状态监听，②属性委托对应的VB
 * 4.UI状态绑定->当数据的state变化时，对应的UI跟随状态改变
 */
class ConversationListAdapter : BaseListAdapter<Conversation, LayItemConversationBinding>() {

    /**
     * 初始化适配器
     */
    override fun init(parent: ViewGroup): LayItemConversationBinding {
        setStateListener(this)
        val viewBinding by parent.viewBindings<LayItemConversationBinding>(parent)
        return viewBinding
    }

    /**
     * UI状态绑定
     */
    override fun setState(
        item: Conversation,
        viewBinding: LayItemConversationBinding,
        position: Int
    ) {
        viewBinding.title.text = item.name
        viewBinding.time.text = item.messageTime
        viewBinding.tvContent.text = item.messageContent
        viewBinding.tvUnreadCount.text = item.messageUnreadCount.toString()
        viewBinding.tvUnreadCount.isVisible = item.messageUnreadCount != 0
        viewBinding.messageAvatar.loadImage(item.avatar, R.mipmap.logo)
    }
}