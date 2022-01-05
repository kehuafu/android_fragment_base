package com.example.demo.chat.bean

import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.example.demo.app.AppManager
import com.kehuafu.base.core.redux.IState
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMValueCallback

open class Message(
    val mid: String? = "",
    val uid: String? = "",
    val avatar: String? = "",
    val name: String? = "",
    val messageContent: String? = "",
    val videoUrl: String = "",
    val imageUlr: String = "",
    val messageType: Int = MSG_TYPE_TEXT,
    val messageSender: Boolean = false,
    val messageTime: String? = "",
    val showTime: Boolean? = true,
    var loading: Boolean = false,
    var sendFailed: Boolean = false,
    val v2TIMMessage: V2TIMMessage
) : IState {

    companion object {
        const val MSG_TYPE_TEXT = 0x01
        const val MSG_TYPE_IMAGE = 0x03
        const val MSG_TYPE_SOUND = 0x04
        const val MSG_TYPE_VIDEO = 0x05
        const val MSG_TYPE_FILE = 0x06
        const val MSG_TYPE_LOCATION = 0x07
        const val MSG_TYPE_FACE = 0x08

        const val V2TIM_MSG_STATUS_SENDING = 1
        const val V2TIM_MSG_STATUS_SUCCESS = 2
        const val V2TIM_MSG_STATUS_SEND_FAILED = 3
        const val V2TIM_MSG_STATUS_DELETED = 4
        const val V2TIM_MSG_STATUS_LOCAL_IMPORTED = 5
        const val V2TIM_MSG_STATUS_REVOKED = 6

        @JvmStatic
        fun messageContent(v2TIMMessage: V2TIMMessage): String {
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

        @JvmStatic
        suspend fun getVideoUrl(v2TIMMessage: V2TIMMessage): String {
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

        @JvmStatic
        fun getImageUrl(v2TIMMessage: V2TIMMessage): String {
            return if (v2TIMMessage.elemType == MSG_TYPE_IMAGE) {
                v2TIMMessage.imageElem.imageList[0].url
            } else {
                ""
            }
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


}