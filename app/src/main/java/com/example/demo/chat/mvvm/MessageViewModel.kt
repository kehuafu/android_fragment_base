package com.example.demo.chat.mvvm

import android.util.Log
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.example.demo.R
import com.example.demo.app.App
import com.example.demo.app.AppManager
import com.kehuafu.base.core.container.widget.toast.showToast
import com.example.demo.base.BaseRequestViewModel
import com.example.demo.chat.bean.Message
import com.example.demo.chat.bean.MessageTheme
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
        messageList = mutableListOf(),
        messageTheme = mutableListOf()
    ),
    reducers = listOf(reducer())
) {
    companion object {
        private fun reducer(): Reducer<MessageState> {
            return { state, action ->
                when (action) {
                    is MessageAction.C2CHistoryMessageList -> {
                        state.copy(messageList = action.messageList, currentAction = action)
                    }
                    is MessageAction.MsgSendFailed -> {
                        state.copy(messageList = action.messageList, currentAction = action)
                    }
                    is MessageAction.MsgSendSuccess -> {
                        state.copy(
                            messageList = action.messageList,
                            currentAction = action
                        )
                    }
                    is MessageAction.InitMessageThemeList -> {
                        state.copy(
                            messageTheme = action.messageTheme,
                            currentAction = action
                        )
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun sendTextMsg(text: String, userId: String, messageList: MutableList<Message>) {
        asyncCall({
            showToast(it.errorMsg)
        }) {

            val v2TIMMessage = AppManager.iCloudMessageManager.createTextMessage(
                text
            )
            Log.e("@@", "createTextMessage--->${v2TIMMessage.msgID}")
            val result = sendMessage(v2TIMMessage, userId, messageList)
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

    private suspend fun sendMessage(
        v2TIMMessage: V2TIMMessage,
        userId: String,
        messageList: MutableList<Message>
    ): String {
        return AppManager.iCloudMessageManager.sendMessage(v2TIMMessage, userId,
            "",
            2,
            false,
            null,
            callback = object : V2TIMSendCallback<V2TIMMessage> {
                override fun onSuccess(p0: V2TIMMessage?) {
                    Log.e("@@", "sendMessage:onSuccess-->" + p0!!.msgID)
                    messageList.mapIndexed { index, message ->
                        if (message.mid == p0.msgID) {
                            message.loading = false
                        }
                    }
                    dispatch(MessageAction.MsgSendSuccess(messageList = messageList))
                }

                override fun onError(p0: Int, p1: String?) {
                    Log.e("@@", "sendMessage:onError-->$p1")
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

    fun getC2CHistoryMessageList(uid: String, firstPull: Boolean) {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            val start = System.currentTimeMillis()
            Log.e("@@@", "start--->$start")
            val messageList = mutableListOf<Message>()
            val messages = AppManager.iCloudMessageManager.getC2CHistoryMessageList(uid, firstPull)
            if (messages != null) {
                var lastTemp = TimeUtils.getNowMills()
                var showTemp: Boolean
                for (msg in messages) {
                    val message = Message(
                        mid = msg.msgID,
                        uid = msg.userID,
                        name = msg.nickName,
                        avatar = msg.faceUrl,
                        messageContent = Message.messageContent(msg),
                        messageType = msg.elemType,
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
            Log.e("@@@", "end:耗时--->" + (System.currentTimeMillis() - start))
        }
    }

    fun initMessageThemeList() {
        asyncCall({
            showToast(it.errorMsg)
        }) {
            val messageTheme = initLocalMessageThemeList()
            dispatch(MessageAction.InitMessageThemeList(messageTheme = messageTheme))
        }
    }

    private fun initLocalMessageThemeList(): MutableList<MessageTheme> {
        val tmpThemeList = mutableListOf<MessageTheme>()
        val stringMessageArrays = App.appContext.resources.getStringArray(R.array.chat_file_type)
        stringMessageArrays.forEachIndexed { index, s ->
            val datingTheme = MessageTheme(
                id = index,
                title = s,
                photoUrl = null
            )
            tmpThemeList.add(datingTheme)
        }
        return tmpThemeList
    }

    fun sendImageMsg(path: String, userId: String, messageList: MutableList<Message>) {
        asyncCall({
            showToast(it.errorMsg)
        }) {
            val v2TIMMessage = AppManager.iCloudMessageManager.createImageMessage(path)
            Log.e("@@@@@@", "createImageMessage--->${v2TIMMessage.imageElem.imageList[2].url}")
            val result = sendMessage(v2TIMMessage, userId, messageList)
            Log.e("@@@@@@", "sendMessage--->$result")
            val message = Message(
                mid = result,
                uid = userId,
                loading = true,
                messageContent = path,
                messageType = Message.MSG_TYPE_IMAGE,
                messageSender = true,
                showTime = false,
                v2TIMMessage = v2TIMMessage
            )
            messageList.add(0, message)
            dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
        }
    }

    fun sendVideoMsg(
        path: String,
        snapshotPath: String,
        duration: Int,
        userId: String,
        messageList: MutableList<Message>
    ) {
        asyncCall({
            showToast(it.errorMsg)
        }) {
            Log.e("@@@@@@", "sendVideoMsg--->${path}")
            val v2TIMMessage = AppManager.iCloudMessageManager.createVideoMessage(
                path, path.substringAfterLast("."), duration, snapshotPath
            )
            val result = sendMessage(v2TIMMessage, userId, messageList)
            Log.e("@@@@@@", "sendMessage--->$result")
            val message = Message(
                mid = result,
                uid = userId,
                loading = true,
                messageContent = snapshotPath,
                messageType = Message.MSG_TYPE_VIDEO,
                messageSender = true,
                showTime = false,
                v2TIMMessage = v2TIMMessage
            )
            messageList.add(0, message)
            dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
        }
    }

    fun sendSoundMsg(
        path: String, duration: Int, userId: String, messageList: MutableList<Message>
    ) {
        asyncCall({
            showToast(it.errorMsg)
        }) {
            Log.e("@@@@@@", "sendSoundMsg--->${path}")
            Log.e("@@", "sendSoundMsg-->${duration}")
            val v2TIMMessage = AppManager.iCloudMessageManager.createSoundMessage(
                path, duration
            )
            val result = sendMessage(v2TIMMessage, userId, messageList)
            Log.e("@@@@@@", "sendMessage--->$result")
            val message = Message(
                mid = result,
                uid = userId,
                loading = true,
                messageContent = duration.toString(),
                messageType = Message.MSG_TYPE_SOUND,
                messageSender = true,
                showTime = false,
                v2TIMMessage = v2TIMMessage
            )
            messageList.add(0, message)
            dispatch(MessageAction.C2CHistoryMessageList(messageList = messageList))
        }
    }

    sealed class MessageAction : Action {
        class C2CHistoryMessageList(val messageList: MutableList<Message>) : MessageAction()
        class MsgSendFailed(val messageList: MutableList<Message>) : MessageAction()
        class MsgSendSuccess(val messageList: MutableList<Message>) : MessageAction()
        class InitMessageThemeList(val messageTheme: MutableList<MessageTheme>) : MessageAction()
    }

    data class MessageState(
        val messageList: MutableList<Message>,
        val messageTheme: MutableList<MessageTheme>,
        val currentAction: Action? = null
    ) : IState
}