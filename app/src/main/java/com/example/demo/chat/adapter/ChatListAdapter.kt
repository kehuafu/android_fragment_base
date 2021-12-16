package com.example.demo.chat.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
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
class ChatListAdapter : BaseListAdapter<Message, LayItemMessageBinding>() {

    /**
     * 初始化适配器
     */
    override fun init(parent: ViewGroup): LayItemMessageBinding {
        setStateListener(this)
        val viewBinding by parent.viewBindings<LayItemMessageBinding>(parent)
        return viewBinding
    }

    /**
     * UI状态绑定
     */
    @SuppressLint("SetTextI18n")
    override fun setState(item: Message, viewBinding: LayItemMessageBinding) {
        viewBinding.title.text = item.name + item.mid
        viewBinding.time.text = item.messageTime
        viewBinding.tvContent.text = item.messageContent
    }
}