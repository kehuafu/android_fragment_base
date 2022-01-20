package com.example.demo.chat.bean

import android.app.Activity
import android.content.Context
import android.graphics.Picture
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.demo.R
import com.example.demo.chat.mvvm.MessageViewModel
import com.example.demo.utils.PathUtil
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.redux.IState
import com.kelin.photoselector.PhotoSelector
import com.kelin.photoselector.model.Photo
import com.kelin.photoselector.model.PhotoImpl
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

    fun openPictureSelector(context: Context, result: (photos: List<Photo>) -> Unit) {
        when (title) {
            "相册" -> {
                PhotoSelector.openPictureSelector(context) { photos ->
                    if (photos.isNullOrEmpty()) {
                        showToast("选择已被取消")
                    } else {
                        result.invoke(photos)
                    }
                }
            }
            "拍摄" -> {
                PhotoSelector.takePhoto(context as Activity) {
                    Log.e("PhotoSelector", "openPictureSelector: $it")
                    if (it != null) {
                        result.invoke(listOf(PhotoImpl(it.absolutePath)))
                    } else {
                        showToast("已取消拍照")
                    }
                }
            }
            "文件" -> {
//                mActivityResultLauncherUtils.launchVideoPick()
            }
        }
    }
}