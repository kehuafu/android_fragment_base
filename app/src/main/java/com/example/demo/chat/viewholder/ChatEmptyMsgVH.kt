package com.example.demo.chat.viewholder

import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatEmptyMsgBinding
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4

class ChatEmptyMsgVH(override val viewBinding: LayItemChatEmptyMsgBinding) :
    BaseRecyclerViewAdapterV4.BaseViewHolder<Message>(
        viewBinding
    ) {
    override fun setState(item: Message, position: Int) {
        super.setState(item, position)
    }
}