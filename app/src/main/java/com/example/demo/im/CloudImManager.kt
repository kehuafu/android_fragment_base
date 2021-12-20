package com.example.demo.im

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.demo.app.App
import com.tencent.imsdk.v2.*


class CloudImManager private constructor() : ICloudImManager,
    AndroidViewModel(App.appContext as Application) {

    companion object {
        private val TAG = CloudImManager::class.java.simpleName

        @JvmStatic
        fun create(): ICloudImManager = CloudImManager()
    }

    override fun initSDK(context: Context, sdkAppID: Int) {
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_INFO
        V2TIMManager.getInstance().initSDK(context, sdkAppID, config)
    }

    override fun addIMSDKListener(listener: V2TIMSDKListener) {
        V2TIMManager.getInstance().addIMSDKListener(object : V2TIMSDKListener() {
            override fun onConnecting() {
                super.onConnecting()
                Log.e(TAG, "onConnecting")
                listener.onConnecting()
            }

            override fun onConnectSuccess() {
                super.onConnectSuccess()
                Log.e(TAG, "onConnectSuccess")
                listener.onConnectSuccess()
            }

            override fun onConnectFailed(code: Int, error: String?) {
                super.onConnectFailed(code, error)
                Log.e(TAG, "onConnectFailed---->code=$code,error=$error")
                listener.onConnectFailed(code, error)
            }

            override fun onKickedOffline() {
                super.onKickedOffline()
                Log.e(TAG, "onKickedOffline---->被踢下线了")
                listener.onKickedOffline()
            }

            override fun onUserSigExpired() {
                super.onUserSigExpired()
                Log.e(TAG, "onUserSigExpired---->在线时票据过期")
                listener.onUserSigExpired()
            }

            override fun onSelfInfoUpdated(info: V2TIMUserFullInfo?) {
                super.onSelfInfoUpdated(info)
                Log.e(TAG, "onSelfInfoUpdated---->登录用户的资料发生了更新")
                listener.onSelfInfoUpdated(info)
            }
        })
    }

    override fun removeIMSDKListener(listener: V2TIMSDKListener) {
        V2TIMManager.getInstance().removeIMSDKListener(listener)
    }

    override fun login(userID: String, userSig: String, callback: V2TIMCallback) {
        if (V2TIMManager.getInstance().loginStatus == V2TIMManager.V2TIM_STATUS_LOGOUT) {
            V2TIMManager.getInstance().login(userID, userSig, callback)
        }
    }

    override fun logout(callback: V2TIMCallback) {
        V2TIMManager.getInstance().logout(callback)
    }
}