package com.example.demo.chat.bean

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.example.demo.R
import com.kehuafu.base.core.redux.IState
import com.tencent.imsdk.v2.V2TIMMessage

class MessageTheme(
    val id: Int,
    val title: String = "",
    val photoUrl: Drawable? = null,
) : IState {
    @DrawableRes
    fun toThemeDrawId(): Int {
        return when (title) {
            "相册" -> {
                R.drawable.chat_file_image_icon
            }
            "拍摄" -> {
                R.drawable.chat_file_photo_icon
            }
            "视频通话" -> {
                R.drawable.chat_file_video_call_icon
            }
            "位置" -> {
                R.drawable.chat_file_location_icon
            }
            "红包" -> {
                R.drawable.chat_file_envelopes_icon
            }
            "转账" -> {
                R.drawable.chat_file_transfer_icon
            }
            "文件" -> {
                R.drawable.chat_file_file_icon
            }
            "我的收藏" -> {
                R.drawable.chat_file_collect_icon
            }
            else -> {
                R.drawable.ic_none_drawable
            }
        }
    }
}