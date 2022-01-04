package com.example.demo.fragment.mine.mvvm

import android.util.Log
import com.example.demo.app.AppManager
import com.kehuafu.base.core.container.widget.toast.showToast
import com.example.demo.base.BaseRequestViewModel
import com.kehuafu.base.core.ktx.asyncCall
import com.kehuafu.base.core.redux.Action
import com.kehuafu.base.core.redux.IState
import com.kehuafu.base.core.redux.Reducer
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMUserFullInfo


class MineViewModel : BaseRequestViewModel<MineViewModel.MineState>(
    initialState = MineState(),
    reducers = listOf(reducer())
) {
    companion object {
        private fun reducer(): Reducer<MineState> {
            return { state, action ->
                when (action) {
                    is MineAction.ModifySuccess -> {
                        state.copy(
                            modify = action.modify
                        )
                    }
                    is MineAction.GetSelfInfo -> {
                        state.copy(
                            selfInfo = action.info
                        )
                    }
                    else -> {
                        state
                    }
                }
            }
        }
    }

    fun setSelfInfo(userNickName: String, userAvatar: String?) {
        httpAsyncCall({
            showToast(it.errorMsg)
        }) {
            Log.d("TAG", "setSelfInfo: $userNickName$userAvatar")
            val userInfo = V2TIMUserFullInfo()
            userInfo.setNickname(userNickName)
            if (userAvatar!!.isNotEmpty()) {
                userInfo.faceUrl = userAvatar
            }
            AppManager.iCloudUserManager.setSelfInfo(userInfo, object : V2TIMCallback {
                override fun onSuccess() {
                    dispatch(MineAction.ModifySuccess(modify = true))

                }

                override fun onError(p0: Int, p1: String?) {
                    Log.d("TAG", "setSelfInfo--->onError: $p1")
                    dispatch(MineAction.ModifySuccess(modify = false))
                }
            })
        }
    }

    fun getSelfInfo() {
        asyncCall({
            showToast(it.errorMsg)
        }) {
            val user: MutableList<String> = mutableListOf()
            user.add(AppManager.currentUserID)
            val info = AppManager.iCloudUserManager.getUsersInfo(user)
            if (info.isNotEmpty()) {
                Log.d("TAG", "getSelfInfo: ${info[0].nickName}")
                dispatch(MineAction.GetSelfInfo(info = info[0]))
            }
        }
    }

    sealed class MineAction : Action {
        class ModifySuccess(val modify: Boolean = false) : MineAction()
        class GetSelfInfo(val info: V2TIMUserFullInfo) : MineAction()
    }

    data class MineState(
        var modify: Boolean = false,
        val selfInfo: V2TIMUserFullInfo? = null
    ) : IState
}