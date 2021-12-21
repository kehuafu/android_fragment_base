package com.example.demo.im

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.demo.app.App
import com.kehuafu.base.core.network.error.ErrorResponse
import com.tencent.imsdk.v2.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudConversationManager private constructor() : ICloudConversationManager,
    AndroidViewModel(App.appContext as Application) {

    companion object {
        private val TAG = CloudConversationManager::class.java.simpleName

        @JvmStatic
        fun create(): CloudConversationManager = CloudConversationManager()
    }

    override suspend fun getConversationList(
        nextSeq: Long,
        count: Int,
        callback: V2TIMValueCallback<V2TIMConversationResult>?
    ): List<V2TIMConversation> {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {

            }
            V2TIMManager.getConversationManager().getConversationList(
                nextSeq,
                count,
                object : V2TIMValueCallback<V2TIMConversationResult> {
                    override fun onSuccess(p0: V2TIMConversationResult?) {
                        continuation.resume(p0!!.conversationList)
                        Log.e("@@", "getConversationList:onSuccess--->" + p0.conversationList)
                    }

                    override fun onError(p0: Int, p1: String?) {
                        Log.e("@@", "getConversationList:onError--->$p1")
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

    override fun deleteConversation(conversationID: String, callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun getTotalUnreadMessageCount(callback: V2TIMValueCallback<Long>) {
        TODO("Not yet implemented")
    }

    override fun addConversationListener(listener: V2TIMConversationListener) {
        V2TIMManager.getConversationManager().addConversationListener(listener)
    }

    override fun removeConversationListener(listener: V2TIMConversationListener) {
        V2TIMManager.getConversationManager().removeConversationListener(listener)
    }
}