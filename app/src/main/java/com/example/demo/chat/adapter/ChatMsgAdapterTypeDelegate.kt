package com.example.demo.chat.adapter

import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatEmptyMsgBinding
import com.example.demo.databinding.LayItemChatImageMsgBinding
import com.example.demo.databinding.LayItemChatTextMsgBinding
import com.kehuafu.base.core.ktx.viewBindings

class ChatMsgAdapterTypeDelegate {

    companion object {
        private const val MSG_TYPE_TEXT = 0x01
        private const val MSG_TYPE_IMAGE = 0x03
        private const val MSG_TYPE_SOUND = 0x04
        private const val MSG_TYPE_VIDEO = 0x05
        private const val MSG_TYPE_FILE = 0x06
        private const val MSG_TYPE_LOCATION = 0x07
        private const val MSG_TYPE_FACE = 0x08
        private const val MSG_UNKNOWN_IN_MSG = -1 //未知消息
    }

    fun getItemViewType(item: Message): Int {
        return when (item.messageType) {
            Message.MSG_TYPE_TEXT -> {
                MSG_TYPE_TEXT
            }
            Message.MSG_TYPE_IMAGE -> {
                MSG_TYPE_IMAGE
            }
            Message.MSG_TYPE_SOUND -> {
                MSG_TYPE_SOUND
            }
            Message.MSG_TYPE_VIDEO -> {
                MSG_TYPE_VIDEO
            }
            Message.MSG_TYPE_FILE -> {
                MSG_TYPE_FILE
            }
            Message.MSG_TYPE_LOCATION -> {
                MSG_TYPE_LOCATION
            }
            Message.MSG_TYPE_FACE -> {
                MSG_TYPE_FACE
            }
            else -> {
                MSG_UNKNOWN_IN_MSG
            }
        }
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBinding {
        return when (viewType) {
            MSG_TYPE_TEXT -> {
                val viewBinding by parent.viewBindings<LayItemChatTextMsgBinding>(parent)
                viewBinding
            }
            MSG_TYPE_IMAGE -> {
                val viewBinding by parent.viewBindings<LayItemChatImageMsgBinding>(parent)
                viewBinding
            }
            else -> {
                val viewBinding by parent.viewBindings<LayItemChatEmptyMsgBinding>(parent)
                viewBinding
            }
        }
    }
}