package com.example.demo.im

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.demo.app.App
import com.kehuafu.base.core.network.error.ErrorResponse
import com.tencent.imsdk.v2.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class CloudUserManager private constructor() : ICloudUserManager,
    AndroidViewModel(App.appContext as Application) {

    companion object {
        private val TAG = CloudUserManager::class.java.simpleName

        @JvmStatic
        fun create(): ICloudUserManager = CloudUserManager()
    }

    override fun setSelfInfo(info: V2TIMUserFullInfo, callback: V2TIMCallback?) {
        V2TIMManager.getInstance().setSelfInfo(info, callback)
    }

    override suspend fun getUsersInfo(
        userIDLis: List<String>
    ): List<V2TIMUserFullInfo> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getInstance()
                .getUsersInfo(userIDLis, object : V2TIMValueCallback<List<V2TIMUserFullInfo>> {
                    override fun onSuccess(p0: List<V2TIMUserFullInfo>?) {
                        continuation.resume(p0!!)
                    }

                    override fun onError(p0: Int, p1: String?) {
                        continuation.resumeWithException(
                            CloudException(
                                ErrorResponse.createError(
                                    error = p1!!,
                                    code = p0
                                )
                            )
                        )
                    }
                })
        }
    }

    override suspend fun getFriendList(): List<V2TIMFriendInfo> {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getFriendshipManager()
                .getFriendList(object : V2TIMValueCallback<List<V2TIMFriendInfo>> {
                    override fun onSuccess(p0: List<V2TIMFriendInfo>?) {
                        continuation.resume(p0!!)
                    }

                    override fun onError(p0: Int, p1: String?) {
                        continuation.resumeWithException(
                            CloudException(
                                ErrorResponse.createError(
                                    error = p1!!,
                                    code = p0
                                )
                            )
                        )
                    }
                })
        }
    }

    override fun addFriend(
        application: V2TIMFriendAddApplication,
        callback: V2TIMValueCallback<V2TIMFriendOperationResult>
    ) {
        V2TIMManager.getFriendshipManager().addFriend(application, callback)
    }
}