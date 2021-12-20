package com.example.demo.fragment.conversation.mvvm

import android.util.Log
import com.blankj.utilcode.util.TimeUtils
import com.example.demo.app.AppManager
import com.kehuafu.base.core.container.widget.toast.showToast
import com.example.demo.base.BaseRequestViewModel
import com.example.demo.chat.bean.Message
import com.kehuafu.base.core.redux.Action
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.redux.Reducer
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback
import com.tencent.imsdk.v2.V2TIMValueCallback


class MessageViewModel : BaseRequestViewModel<MessageViewModel.MessageState>(
    initialState = MessageState(
        conversationList = mutableListOf(),
        messageList = mutableListOf()
    ),
    reducers = listOf(reducer())
) {
    companion object {
        private fun reducer(): Reducer<MessageState> {
            return { state, action ->
                when (action) {
                    is MessageAction.Success -> {
                        state.copy(conversationList = action.conversationList)
                    }
                    is MessageAction.C2CHistoryMessageList -> {
                        state.copy(messageList = action.messageList)
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun sendMsg(text: String, userId: String) {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            AppManager.iCloudMessageManager.sendMessage(AppManager.iCloudMessageManager.createTextMessage(
                text
            ),
                userId, "", 2, false, null, callback = object : V2TIMSendCallback<V2TIMMessage> {
                    override fun onSuccess(p0: V2TIMMessage?) {
                        Log.e("@@", "onSuccess-->" + p0!!.userID)
                    }

                    override fun onError(p0: Int, p1: String?) {
                        Log.e("@@", "onError-->$p1")
                    }

                    override fun onProgress(p0: Int) {
                        Log.e("@@", "onProgress-->$p0")
                    }

                })
        }
    }

    fun getC2CHistoryMessageList(uid: String) {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            val messageList = mutableListOf<Message>()
            AppManager.iCloudMessageManager.getC2CHistoryMessageList(uid,
                object : V2TIMValueCallback<List<V2TIMMessage>> {
                    override fun onSuccess(p0: List<V2TIMMessage>?) {
                        for (msg in p0!!) {
                            val message = Message(
                                mid = msg.msgID,
                                uid = msg.userID,
                                name = msg.nickName,
                                avatar = msg.faceUrl,
                                messageContent = msg.textElem.text,
                                messageType = 0,
                                messageTime = TimeUtils.date2String(TimeUtils.millis2Date(msg.timestamp)),
                                messageSender = msg.sender == AppManager.currentUserID
                            )
                            messageList.add(message)
                        }
                        dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
                    }

                    override fun onError(p0: Int, p1: String?) {
                        dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
                    }
                })
        }
    }

    fun getConversationList() {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            val messageList = mutableListOf<Message>()
            val conversationList =
                AppManager.iCloudConversationManager.getConversationList(0, 100, null)
            Log.e("@@", "conversationList---->" + conversationList.size)

            for (conversation in conversationList) {
                val message = Message(
                    mid = conversation.conversationID,
                    uid = "",
                    name = conversation.showName,
                    avatar = "",
                    messageContent = conversation.lastMessage.textElem.text,
                    messageType = 0,
                    messageTime = TimeUtils.date2String(TimeUtils.millis2Date(conversation.lastMessage.timestamp)),
                    messageSender = conversation.userID == AppManager.currentUserID
                )
                messageList.add(message)
            }
            //请求后台数据
//            val token = AppManager.apiService.test(testBody = TestBody(uid = uid, token = token))
            dispatch(MessageAction.Success(conversationList = messageList))
        }
    }

    fun sendMsg(text: String, conversationList: MutableList<Message>) {
        conversationList.add(0, Message(messageContent = text, messageSender = true))
        dispatch(MessageAction.Success(conversationList = conversationList))
    }

    sealed class MessageAction : Action {
        class Success(val conversationList: MutableList<Message>) : MessageAction()
        class C2CHistoryMessageList(val messageList: MutableList<Message>) : MessageAction()
    }

    data class MessageState(
        val conversationList: MutableList<Message>,
        val messageList: MutableList<Message>
    ) : IState {

    }
}