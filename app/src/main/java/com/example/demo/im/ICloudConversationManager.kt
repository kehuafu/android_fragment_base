package com.example.demo.im

import com.tencent.imsdk.v2.*

interface ICloudConversationManager {

    suspend fun getConversationList(
        nextSeq: Long,
        count: Int,
        callback:
        V2TIMValueCallback<V2TIMConversationResult>?
    ): List<V2TIMConversation>

    fun deleteConversation(conversationID: String, callback: V2TIMCallback)

    fun getTotalUnreadMessageCount(callback: V2TIMValueCallback<Long>)
}