package com.example.demo.im

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.demo.app.App
import com.kehuafu.base.core.network.error.ErrorResponse
import com.tencent.imsdk.v2.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudMessageManager private constructor() : ICloudMessageManager,
    AndroidViewModel(App.appContext as Application) {

    companion object {
        private val TAG = CloudMessageManager::class.java.simpleName

        @JvmStatic
        fun create(): CloudMessageManager = CloudMessageManager()
    }

    override fun createTextMessage(text: String): V2TIMMessage {
        return V2TIMManager.getMessageManager().createTextMessage(text)
    }

    override fun createImageMessage(imagePath: String): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override fun createSoundMessage(soundPath: String, duration: Int): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override fun createVideoMessage(
        videoFilePath: String,
        type: String,
        duration: Int,
        snapshotPath: String
    ): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override fun createFileMessage(filePath: String, fileName: String): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override fun createLocationMessage(
        desc: String,
        longitude: Double,
        latitude: Double
    ): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override fun createFaceMessage(index: Int, data: Byte): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override fun sendMessage(
        message: V2TIMMessage,
        receiver: String?,
        groupID: String?,
        priority: Int,
        onlineUserOnly: Boolean?,
        offlinePushInfo: V2TIMOfflinePushInfo?,
        callback: V2TIMSendCallback<V2TIMMessage>
    ): String {
        return V2TIMManager.getMessageManager()
            .sendMessage(message, receiver, groupID, priority, false, null, callback)
    }

    override fun revokeMessage(msg: V2TIMMessage, callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun markC2CMessageAsRead(userID: String, callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun markGroupMessageAsRead(groupID: String, callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun markAllMessageAsRead(callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun addAdvancedMsgListener(listener: V2TIMAdvancedMsgListener) {
        TODO("Not yet implemented")
    }

    override fun removeAdvancedMsgListener(listener: V2TIMAdvancedMsgListener) {
        TODO("Not yet implemented")
    }

    override suspend fun getC2CHistoryMessageList(userID: String): List<V2TIMMessage>? {
        return suspendCancellableCoroutine { continuation ->
            V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                userID,
                20,
                null,
                object : V2TIMValueCallback<List<V2TIMMessage>> {
                    override fun onSuccess(p0: List<V2TIMMessage>?) {
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
}