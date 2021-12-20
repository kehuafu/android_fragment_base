package com.example.demo.im

import android.content.Context
import com.tencent.imsdk.v2.*

interface ICloudImManager {

    fun initSDK(context: Context, sdkAppID: Int)

    fun addIMSDKListener(listener: V2TIMSDKListener)

    fun removeIMSDKListener(listener: V2TIMSDKListener)

    fun login(userID: String, userSig: String, callback: V2TIMCallback)

    fun logout(callback: V2TIMCallback)

}