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
import com.kehuafu.base.core.ktx.runOnMainThread
import com.kehuafu.base.core.redux.Action
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.redux.Reducer
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback
import com.tencent.imsdk.v2.V2TIMValueCallback
import java.util.*
import kotlin.concurrent.schedule


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
                        ) >= 60 * 5L
                    )
                    showTemp = message.showTime!!
                    if (showTemp) {
                        lastTemp = msg.timestamp * 1000
                    }
                    messageList.add(message)
                }
            }
            Timer().schedule(200) {
                dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
            }
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

    fun sendMsg(text: String, conversationList: MutableList<Conversation>) {
        conversationList.add(0, Conversation(messageContent = text, messageSender = true))
        dispatch(MessageAction.Success(conversationList = conversationList))
    }

    sealed class MessageAction : Action {
        class Success(val conversationList: MutableList<Conversation>) : MessageAction()
        class C2CHistoryMessageList(val messageList: MutableList<Message>) : MessageAction()
    }

    data class MessageState(
        val conversationList: MutableList<Conversation>,
        val messageList: MutableList<Message>
    ) : IState {

    }
}