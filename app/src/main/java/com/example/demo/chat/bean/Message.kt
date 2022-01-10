package com.example.demo.chat.bean

import android.util.Log
import com.example.demo.app.AppManager
import com.kehuafu.base.core.container.widget.toast.showToast
import com.tencent.imsdk.v2.V2TIMMessage

open class Message(
    val mid: String? = "",
    val uid: String? = "",
    val avatar: String? = "",
    val name: String? = "",
    val messageContent: String? = "",
    val messageType: Int = MSG_TYPE_TEXT,
    val messageSender: Boolean = false,
    val messageTime: String? = "",
    val showTime: Boolean? = true,
    var loading: Boolean = false,
    var sendFailed: Boolean = false,
    val videoUrl: String = "",
    val imageUlr: String = "",
    val soundUlr: String = "",
    val v2TIMMessage: V2TIMMessage
) : IMessage<V2TIMMessage>(message = v2TIMMessage) {

    companion object {

        const val V2TIM_MSG_STATUS_SENDING = 1
        const val V2TIM_MSG_STATUS_SUCCESS = 2
        const val V2TIM_MSG_STATUS_SEND_FAILED = 3
        const val V2TIM_MSG_STATUS_DELETED = 4
        const val V2TIM_MSG_STATUS_LOCAL_IMPORTED = 5
        const val V2TIM_MSG_STATUS_REVOKED = 6

        fun build(v2TIMMessage: V2TIMMessage): Message {
            return Message(v2TIMMessage = v2TIMMessage)
        }
    }

    fun secToTime(seconds: Int): String {
        val hour = seconds / 3600
        val minute = (seconds - hour * 3600) / 60
        val second = seconds - hour * 3600 - minute * 60
        val sb = StringBuffer()
        if (hour > 0) {
            if (hour <= 9) {
                sb.append("0")
            }
            sb.append("$hour:")
        }
        if (minute > 0) {
            if (minute <= 9) {
                sb.append("0$minute:")
            } else {
                sb.append("$minute:")
            }
        } else {
            sb.append("00:")
        }
        if (second > 0) {
            if (second <= 9) {
                sb.append("0$second")
            } else {
                sb.append("$second")
            }
        }
        if (second == 0) {
            sb.append("00:00")
        }
        return sb.toString()
    }

    fun isVideo(): Boolean {
        return messageType == MSG_TYPE_VIDEO
    }

    fun isImage(): Boolean {
        return messageType == MSG_TYPE_IMAGE
    }

    override suspend fun getSoundUrl(): String {
        val v2TIMMessage = getMessageObject()
        return if (v2TIMMessage.elemType == MSG_TYPE_SOUND) {
            if (v2TIMMessage.soundElem.path.isNotEmpty()) {
                v2TIMMessage.soundElem.path
            } else {
                AppManager.iCloudMessageManager.getSoundUrl(v2TIMMessage)!!
            }
        } else {
            ""
        }
    }

    override suspend fun getSnapshotUrl(): String {
        val v2TIMMessage = getMessageObject()
        return if (v2TIMMessage.elemType == MSG_TYPE_VIDEO) {
            if (v2TIMMessage.videoElem.snapshotPath.isNotEmpty()) {
                v2TIMMessage.videoElem.snapshotPath
            } else {
                AppManager.iCloudMessageManager.getSnapshotUrl(v2TIMMessage)!!
            }
        } else {
            ""
        }
    }

    override suspend fun getVideoUrl(): String {
        return if (v2TIMMessage.elemType == MSG_TYPE_VIDEO) {
            if (v2TIMMessage.videoElem.videoPath.isNotEmpty()) {
                v2TIMMessage.videoElem.videoPath
            } else {
                AppManager.iCloudMessageManager.getVideoUrl(v2TIMMessage)!!
            }
        } else {
            ""
        }
    }

    override fun getImageUrl(): String {
        return if (v2TIMMessage.elemType == MSG_TYPE_IMAGE) {
            v2TIMMessage.imageElem.imageList[0].url
        } else {
            ""
        }
    }

    override fun messageContent(): String {
        return when (v2TIMMessage.elemType) {
            MSG_TYPE_TEXT -> {
                v2TIMMessage.textElem.text
            }
            MSG_TYPE_IMAGE -> {
                return if (v2TIMMessage.status == V2TIM_MSG_STATUS_SEND_FAILED) {
                    v2TIMMessage.imageElem.path
                } else {
                    v2TIMMessage.imageElem.imageList[2].url
                }
            }
            MSG_TYPE_SOUND -> {
                Log.e("@@", "messageContent-->${v2TIMMessage.soundElem.duration}")
                v2TIMMessage.soundElem.duration.toString()
            }
            MSG_TYPE_VIDEO -> {
                v2TIMMessage.videoElem.snapshotPath
            }
            MSG_TYPE_FACE -> {
                "[动画表情]"
            }
            MSG_TYPE_FILE -> {
                "[文件]"
            }
            else -> {
                "[其他消息]"
            }
        }
    }
}