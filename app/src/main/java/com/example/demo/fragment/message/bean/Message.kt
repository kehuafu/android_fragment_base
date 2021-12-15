package com.example.demo.fragment.message.bean

import com.kehuafu.base.core.redux.IState

class Message(
    var mid: String,
    val uid: String,
    val avatar: String,
    val name: String,
    val messageContent: String,
    val messageType: Int,
    val messageTime: String
) : IState {
}