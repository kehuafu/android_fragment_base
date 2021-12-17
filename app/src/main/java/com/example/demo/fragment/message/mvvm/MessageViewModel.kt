package com.example.demo.fragment.message.mvvm

import com.kehuafu.base.core.container.widget.toast.showToast
import com.example.demo.base.BaseRequestViewModel
import com.example.demo.fragment.message.bean.Message
import com.kehuafu.base.core.redux.Action
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.redux.Reducer


class MessageViewModel : BaseRequestViewModel<MessageViewModel.MessageState>(
    initialState = MessageState(
        messageList = mutableListOf()
    ),
    reducers = listOf(reducer())
) {
    companion object {
        private fun reducer(): Reducer<MessageState> {
            return { state, action ->
                when (action) {
                    is MessageAction.Success -> {
                        state.copy(messageList = action.messageList)
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun getMessage(uid: String) {
        httpAsyncCall({
            showToast(it.errorCode.toString())
        }) {


            val messageList = mutableListOf<Message>()
            for (i in 0..20) {
                val message = Message(
                    mid = i.toString(),
                    uid = uid,
                    name = "用户名",
                    avatar = "",
                    messageContent = "消息内容",
                    messageType = 0,
                    messageTime = "16:37:45",
                    messageSender = i % 2 == 0
                )
                messageList.add(message)
            }
            //请求后台数据
//            val token = AppManager.apiService.test(testBody = TestBody(uid = uid, token = token))
            dispatch(MessageAction.Success(messageList = messageList))
        }
    }

    fun sendMsg(text: String, messageList: MutableList<Message>) {
        messageList.add(0, Message(messageContent = text, messageSender = true))
        dispatch(MessageAction.Success(messageList = messageList))
    }

    sealed class MessageAction : Action {
        class Success(val messageList: MutableList<Message>) : MessageAction()
    }

    data class MessageState(val messageList: MutableList<Message>) : IState {

    }
}