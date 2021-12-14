package com.example.demo.message.bean

import com.kehuafu.base.core.redux.IState

class Message(
    val mid: String,
    val uid: String,
    val avatar: String,
    val name: String,
    val messageContent: String,
    val messageType: Int,
    val messageTime: String
) : IState {
}