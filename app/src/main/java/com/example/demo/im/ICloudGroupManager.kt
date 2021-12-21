package com.example.demo.im

import com.tencent.imsdk.v2.V2TIMCreateGroupMemberInfo
import com.tencent.imsdk.v2.V2TIMGroupInfo
import com.tencent.imsdk.v2.V2TIMGroupMemberOperationResult
import com.tencent.imsdk.v2.V2TIMValueCallback

interface ICloudGroupManager {

    fun createGroup(
        info: V2TIMGroupInfo,
        memberList: List<V2TIMCreateGroupMemberInfo>,
        callback: V2TIMValueCallback<String>
    )

    fun inviteUserToGroup(
        groupID: String,
        userList: List<String>,
        callback: V2TIMValueCallback<List<V2TIMGroupMemberOperationResult>>
    )

    fun kickGroupMember(
        groupID: String,
        memberList: List<String>,
        reason: String,
        callback: V2TIMValueCallback<List<V2TIMGroupMemberOperationResult>>
    )
}