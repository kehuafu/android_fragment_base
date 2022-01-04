package com.example.demo.im

import com.tencent.imsdk.v2.*


interface ICloudUserManager {

    fun setSelfInfo(
        info: V2TIMUserFullInfo,
        callback: V2TIMCallback? = null
    )

    suspend fun getUsersInfo(
        userIDLis: List<String>
    ): List<V2TIMUserFullInfo>

    suspend  fun getFriendList(): List<V2TIMFriendInfo>

    fun addFriend(
        application: V2TIMFriendAddApplication,
        callback: V2TIMValueCallback<V2TIMFriendOperationResult>
    )
}