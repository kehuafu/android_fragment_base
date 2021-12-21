package com.example.demo.im

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.demo.app.App
import com.tencent.imsdk.v2.*


class CloudGroupManager private constructor() : ICloudGroupManager,
    AndroidViewModel(App.appContext as Application) {

    companion object {
        private val TAG = CloudGroupManager::class.java.simpleName

        @JvmStatic
        fun create(): CloudGroupManager = CloudGroupManager()
    }

    override fun createGroup(
        info: V2TIMGroupInfo,
        memberList: List<V2TIMCreateGroupMemberInfo>,
        callback: V2TIMValueCallback<String>
    ) {
        TODO("Not yet implemented")
    }

    override fun inviteUserToGroup(
        groupID: String,
        userList: List<String>,
        callback: V2TIMValueCallback<List<V2TIMGroupMemberOperationResult>>
    ) {
        TODO("Not yet implemented")
    }

    override fun kickGroupMember(
        groupID: String,
        memberList: List<String>,
        reason: String,
        callback: V2TIMValueCallback<List<V2TIMGroupMemberOperationResult>>
    ) {
        TODO("Not yet implemented")
    }
}