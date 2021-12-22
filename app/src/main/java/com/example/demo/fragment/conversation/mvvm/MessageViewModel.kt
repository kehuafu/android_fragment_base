package com.example.demo.fragment.conversation.mvvm

import android.util.Log
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.example.demo.app.AppManager
import com.kehuafu.base.core.container.widget.toast.showToast
import com.example.demo.base.BaseRequestViewModel
import com.example.demo.chat.bean.Message
import com.example.demo.fragment.conversation.bean.Conversation
import com.kehuafu.base.core.ktx.asyncCall
import com.kehuafu.base.core.redux.Action
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.redux.Reducer
import com.tencent.imsdk.message.Message.V2TIM_MSG_STATUS_SEND_FAILED
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_MSG_STATUS_SENDING
import com.tencent.imsdk.v2.V2TIMSendCallback


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
                    is MessageAction.MsgSendFailed -> {
                        state.copy(messageList = action.messageList)
                    }
                    is MessageAction.MsgSendSuccess -> {
                        state.copy(
                            messageList = action.messageList
                        )
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun sendMsg(text: String, userId: String, messageList: MutableList<Message>) {
        asyncCall({
            showToast(it.errorMsg)
        }) {

            val v2TIMMessage = AppManager.iCloudMessageManager.createTextMessage(
                text
            )
            Log.e("@@", "createTextMessage--->${v2TIMMessage.msgID}")
            val result =
                AppManager.iCloudMessageManager.sendMessage(v2TIMMessage, userId,
                    "",
                    2,
                    false,
                    null,
                    callback = object : V2TIMSendCallback<V2TIMMessage> {
                        override fun onSuccess(p0: V2TIMMessage?) {
                            Log.e("@@", "onSuccess-->" + p0!!.msgID)
                            messageList.mapIndexed { index, message ->
                                if (message.mid == p0.msgID) {
                                    message.loading = false
                                }
                            }
                            dispatch(MessageAction.MsgSendSuccess(messageList = messageList))
                        }

                        override fun onError(p0: Int, p1: String?) {
                            Log.e("@@", "onError-->$p1")
                            messageList.mapIndexed { index, message ->
                                if (message.mid == v2TIMMessage.msgID) {
                                    message.loading = false
                                    message.sendFailed = true
                                }
                            }
                            dispatch(MessageAction.MsgSendFailed(messageList = messageList))
                        }

                        override fun onProgress(p0: Int) {
                            Log.e("@@", "onProgress-->$p0")
                        }
                    })
            Log.e("@@", "sendMessage--->$result")
            val message = Message(
                mid = result,
                uid = userId,
                loading = true,
                messageContent = text,
                messageType = Message.MSG_TYPE_TEXT,
                messageSender = true,
                showTime = false,
                v2TIMMessage = v2TIMMessage
            )
            messageList.add(0, message)
            dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
        }
    }

    fun resendMessage(
        message: Message,
        userId: String,
        messageList: MutableList<Message>,
        pos: Int
    ) {
        asyncCall({
            showToast(it.errorMsg)
        }) {
            Log.e("@@", "resendMessage--->${message.mid}")
            AppManager.iCloudMessageManager.sendMessage(message.v2TIMMessage, userId,
                "",
                2,
                false,
                null,
                callback = object : V2TIMSendCallback<V2TIMMessage> {
                    override fun onSuccess(p0: V2TIMMessage?) {
                        Log.e("@@", "onSuccess-->" + p0!!.msgID)
                        message.loading = false
                        messageList[pos] = message
                        MessageAction.MsgSendSuccess(
                            messageList = messageList
                        )
                    }

                    override fun onError(p0: Int, p1: String?) {
                        Log.e("@@", "onError-->$p1")
                        message.loading = false
                        message.sendFailed = true
                        messageList[pos] = message
                        MessageAction.MsgSendFailed(
                            messageList = messageList
                        )
                    }

                    override fun onProgress(p0: Int) {
                        Log.e("@@", "onProgress-->$p0")
                    }
                })
            message.loading = true
            message.sendFailed = false
            messageList[pos] = message
            MessageAction.MsgSendSuccess(
                messageList = messageList
            )
        }
    }

    fun getC2CHistoryMessageList(uid: String) {
        asyncCall {
            val messageList = mutableListOf<Message>()
            val messages = AppManager.iCloudMessageManager.getC2CHistoryMessageList(uid)
            if (messages != null) {
                var lastTemp = TimeUtils.getNowMills()
                var showTemp: Boolean
                for (msg in messages) {
                    val message = Message(
                        mid = msg.msgID,
                        uid = msg.userID,
                        name = msg.nickName,
                        avatar = msg.faceUrl,
                        messageContent = msg.textElem.text,
                        messageType = Message.MSG_TYPE_TEXT,
                        messageTime = TimeUtils.date2String(TimeUtils.millis2Date(msg.timestamp * 1000)),
                        messageSender = msg.sender == AppManager.currentUserID,
                        showTime = lastTemp - TimeUtils.getMillis(
                            msg.timestamp * 1000,
                            60 * 5L,
                            TimeConstants.SEC
                        ) >= 60 * 5L,
                        loading = msg.status == V2TIM_MSG_STATUS_SENDING,
                        sendFailed = msg.status == V2TIM_MSG_STATUS_SEND_FAILED,
                        v2TIMMessage = msg
                    )
                    showTemp = message.showTime!!
                    if (showTemp) {
                        lastTemp = msg.timestamp * 1000
                    }
                    messageList.add(message)
                }
            }
            dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
        }
    }

    fun getConversationList() {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            val conList = mutableListOf<Conversation>()
            val conversationList =
                AppManager.iCloudConversationManager.getConversationList(0, 100, null)
            for (conversation in conversationList) {
                val message = Conversation(
                    mid = conversation.conversationID,
                    uid = "",
                    name = conversation.showName,
                    avatar = "",
                    messageContent = conversation.lastMessage.textElem.text,
                    messageType = 0,
                    messageTime = TimeUtils.date2String(TimeUtils.millis2Date(conversation.lastMessage.timestamp * 1000)),
                    messageSender = conversation.userID == AppManager.currentUserID,
                    messageUnreadCount = conversation.unreadCount
                )
                conList.add(message)
            }
            dispatch(MessageAction.Success(conversationList = conList))
        }
    }

    sealed class MessageAction : Action {
        class Success(val conversationList: MutableList<Conversation>) : MessageAction()
        class C2CHistoryMessageList(val messageList: MutableList<Message>) : MessageAction()
        class MsgSendFailed(val messageList: MutableList<Message>) : MessageAction()
        class MsgSendSuccess(val messageList: MutableList<Message>) : MessageAction()
    }

    data class MessageState(
        val conversationList: MutableList<Conversation>,
        val messageList: MutableList<Message>
    ) : IState {

    }
}