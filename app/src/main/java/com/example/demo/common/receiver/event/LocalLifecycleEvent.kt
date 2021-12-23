package com.example.demo.common.receiver.event

import com.tencent.imsdk.v2.V2TIMConversation
import com.tencent.imsdk.v2.V2TIMMessage


sealed class LocalLifecycleEvent {
    class ReceivedChatMsgEvent(val msg: V2TIMMessage) : LocalLifecycleEvent()
    class ReceivedNewConversationEvent(val conversationList: MutableList<V2TIMConversation>) :
        LocalLifecycleEvent()

    class ReceivedConversationChangedEvent(val conversationList: MutableList<V2TIMConversation>) :
        LocalLifecycleEvent()

    class NetWorkIsConnectedEvent(val conn: Boolean) : LocalLifecycleEvent()
}