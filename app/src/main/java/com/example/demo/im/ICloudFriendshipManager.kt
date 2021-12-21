package com.example.demo.im

import com.tencent.imsdk.v2.V2TIMFriendAddApplication
import com.tencent.imsdk.v2.V2TIMFriendInfo
import com.tencent.imsdk.v2.V2TIMFriendOperationResult
import com.tencent.imsdk.v2.V2TIMValueCallback

interface ICloudFriendshipManager {

    fun addFriend(
        application: V2TIMFriendAddApplication,
        callback: V2TIMValueCallback<V2TIMFriendOperationResult>
    )

    fun getFriendList(callback: V2TIMValueCallback<List<V2TIMFriendInfo>>)

    fun deleteFromFriendList(
        userIDList: List<String>,
        deleteType: Int,
        callback: V2TIMValueCallback<List<V2TIMFriendOperationResult>>
    )
}