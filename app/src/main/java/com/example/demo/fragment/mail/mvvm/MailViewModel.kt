package com.example.demo.fragment.mail.mvvm

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.example.demo.app.AppManager
import com.kehuafu.base.core.container.widget.toast.showToast
import com.example.demo.base.BaseRequestViewModel
import com.kehuafu.base.core.ktx.asyncCall
import com.kehuafu.base.core.redux.Action
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.redux.Reducer
import com.tencent.imsdk.v2.*


class MailViewModel : BaseRequestViewModel<MailViewModel.MailState>(
    initialState = MailState(),
    reducers = listOf(reducer())
) {
    companion object {
        private fun reducer(): Reducer<MailState> {
            return { state, action ->
                when (action) {
                    is MailAction.AddFriendSuccess -> {
                        state.copy(
                            add = action.add
                        )
                    }
                    is MailAction.GetFriendList -> {
                        state.copy(
                            list = action.list
                        )
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun addFriend(userId: String) {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            if (userId.isEmpty()) {
                showToast("userId不能为空")
                return@httpAsyncCall
            }
            val userInfo = V2TIMFriendAddApplication(userId)
            AppManager.iCloudUserManager.addFriend(userInfo, object :
                V2TIMValueCallback<V2TIMFriendOperationResult> {
                override fun onSuccess(p0: V2TIMFriendOperationResult?) {
                    Log.e("p0", "onSuccess--->" + p0!!.resultInfo)
                    dispatch(MailAction.AddFriendSuccess(add = true))
                }

                override fun onError(p0: Int, p1: String?) {
                    showToast("添加失败，用户不存在！")
                    dispatch(MailAction.AddFriendSuccess(add = false))
                }
            })
        }
    }

    fun getFriendList() {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            val list = AppManager.iCloudUserManager.getFriendList()
            LogUtils.a("aaaaaaaa", "getFriendList--->", list)
            if (list.isNotEmpty()) {
                dispatch(MailAction.GetFriendList(list = list))
            }
        }
    }

    sealed class MailAction : Action {
        class AddFriendSuccess(val add: Boolean = false) : MailAction()
        class GetFriendList(val list: List<V2TIMFriendInfo>) : MailAction()
    }

    data class MailState(
        var add: Boolean = false,
        val list: List<V2TIMFriendInfo> = emptyList()
    ) : IState
}