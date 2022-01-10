package com.example.demo.chat.bean

import com.kehuafu.base.core.redux.IState

abstract class IMessage<T>(
    val message: T
) : IState {
    companion object {
        const val MSG_TYPE_TEXT = 0x01
        const val MSG_TYPE_IMAGE = 0x03
        const val MSG_TYPE_SOUND = 0x04
        const val MSG_TYPE_VIDEO = 0x05
        const val MSG_TYPE_FILE = 0x06
        const val MSG_TYPE_LOCATION = 0x07
        const val MSG_TYPE_FACE = 0x08
    }

    abstract suspend fun getVideoUrl(): String

    abstract suspend fun getSoundUrl(): String

    abstract fun getImageUrl(): String

    abstract suspend fun getSnapshotUrl(): String

    abstract fun messageContent(): String

    open fun getMessageObject(): T {
        return message
    }
}