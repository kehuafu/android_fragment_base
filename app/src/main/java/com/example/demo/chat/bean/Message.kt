package com.example.demo.chat.bean

import com.kehuafu.base.core.redux.IState
import com.tencent.imsdk.v2.V2TIMMessage

open class Message(
    val mid: String? = "",
    val uid: String? = "",
    val avatar: String? = "",
    val name: String? = "",
    val messageContent: String? = "",
    val messageType: Int = MSG_TYPE_TEXT,
    val messageSender: Boolean = false,
    val messageTime: String? = "",
    val showTime: Boolean? = true,
    var loading: Boolean = false,
    var sendFailed: Boolean = false,
    val v2TIMMessage: V2TIMMessage
) : IState {

    companion object {
        const val MSG_TYPE_TEXT = 0x01
        const val MSG_TYPE_IMAGE = 0x02
        const val MSG_TYPE_SOUND = 0x03
        const val MSG_TYPE_VIDEO = 0x04
        const val MSG_TYPE_FILE = 0x05
        const val MSG_TYPE_LOCATION = 0x06
        const val MSG_TYPE_FACE = 0x07
    }
}