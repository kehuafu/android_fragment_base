package com.example.demo.chat.bean

import com.kehuafu.base.core.redux.IState

class Message(
    val mid: String? = "",
    val uid: String? = "",
    val avatar: String? = "",
    val name: String? = "",
    val messageContent: String,
    val messageType: Int? = 0,
    val messageSender: Boolean,
    val messageTime: String? = ""
) : IState {
}