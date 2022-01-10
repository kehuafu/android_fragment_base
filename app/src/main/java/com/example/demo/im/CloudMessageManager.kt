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

class CloudMessageManager private constructor() : ICloudMessageManager,
    AndroidViewModel(App.appContext as Application) {

    companion object {
        private val TAG = CloudMessageManager::class.java.simpleName

        @JvmStatic
        fun create(): CloudMessageManager = CloudMessageManager()
    }

    override suspend fun createTextMessage(text: String): V2TIMMessage {
        return suspendCancellableCoroutine { continuation ->
            continuation.resume(
                V2TIMManager.getMessageManager().createTextMessage(text)
            )
        }
    }

    override suspend fun createImageMessage(imagePath: String): V2TIMMessage {
        return V2TIMManager.getMessageManager().createImageMessage(imagePath)
    }

    override suspend fun createSoundMessage(soundPath: String, duration: Int): V2TIMMessage {
        return suspendCancellableCoroutine { continuation ->
            continuation.resume(
                V2TIMManager.getMessageManager()
                    .createSoundMessage(soundPath, duration)
            )
        }
    }

    override suspend fun createVideoMessage(
        videoFilePath: String,
        type: String,
        duration: Int,
        snapshotPath: String
    ): V2TIMMessage {
        return suspendCancellableCoroutine { continuation ->
            continuation.resume(
                V2TIMManager.getMessageManager()
                    .createVideoMessage(videoFilePath, type, duration, snapshotPath)
            )
        }
    }

    override suspend fun createFileMessage(filePath: String, fileName: String): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override suspend fun createLocationMessage(
        desc: String,
        longitude: Double,
        latitude: Double
    ): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override suspend fun createFaceMessage(index: Int, data: Byte): V2TIMMessage {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(
        message: V2TIMMessage,
        receiver: String?,
        groupID: String?,
        priority: Int,
        onlineUserOnly: Boolean?,
        offlinePushInfo: V2TIMOfflinePushInfo?,
        callback: V2TIMSendCallback<V2TIMMessage>
    ): String {
        return suspendCancellableCoroutine { continuation ->
            continuation.resume(
                V2TIMManager.getMessageManager()
                    .sendMessage(message, receiver, groupID, priority, false, null, callback)
            )
        }
    }

    override fun revokeMessage(msg: V2TIMMessage, callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun markC2CMessageAsRead(userID: String, callback: V2TIMCallback?) {
        V2TIMManager.getMessageManager().markC2CMessageAsRead(userID, callback)
    }

    override fun markGroupMessageAsRead(groupID: String, callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun markAllMessageAsRead(callback: V2TIMCallback) {
        TODO("Not yet implemented")
    }

    override fun addAdvancedMsgListener(listener: V2TIMAdvancedMsgListener) {
        V2TIMManager.getMessageManager().addAdvancedMsgListener(listener)
    }

    override fun removeAdvancedMsgListener(listener: V2TIMAdvancedMsgListener) {
        V2TIMManager.getMessageManager().removeAdvancedMsgListener(listener)
    }

    override suspend fun getC2CHistoryMessageList(
        userID: String,
        firstPull: Boolean?
    ): List<V2TIMMessage>? {
        return suspendCancellableCoroutine { continuation ->
            val options = V2TIMMessageListGetOption()
            options.getType = V2TIMMessageListGetOption.V2TIM_GET_LOCAL_OLDER_MSG
            options.userID = userID
            options.count = 20
            if (firstPull == true) {
                V2TIMManager.getMessageManager()
                    .getHistoryMessageList(
                        options,
                        object : V2TIMValueCallback<List<V2TIMMessage>> {
                            override fun onSuccess(p0: List<V2TIMMessage>?) {
                                continuation.resume(p0!!)
                                Log.d(TAG, "onSuccess: " + p0!!.size)
                            }

                            override fun onError(p0: Int, p1: String?) {
                                Log.d(TAG, "onSuccess: $p1")
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
                return@suspendCancellableCoroutine
            }
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

    override suspend fun getVideoUrl(msg: V2TIMMessage): String? {
        return suspendCancellableCoroutine { continuation ->
            msg.videoElem.getVideoUrl(object : V2TIMValueCallback<String> {
                override fun onSuccess(p0: String?) {
                    continuation.resume(p0)
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

    override suspend fun getSoundUrl(msg: V2TIMMessage): String? {
        return suspendCancellableCoroutine { continuation ->
            msg.soundElem.getUrl(object : V2TIMValueCallback<String> {
                override fun onSuccess(p0: String?) {
                    continuation.resume(p0)
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

    override suspend fun getSnapshotUrl(msg: V2TIMMessage): String? {
        return suspendCancellableCoroutine { continuation ->
            msg.videoElem.getSnapshotUrl(object : V2TIMValueCallback<String> {
                override fun onSuccess(p0: String?) {
                    continuation.resume(p0)
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