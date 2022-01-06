package com.example.demo.utils

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

open class ActivityResultLauncherUtils(mActivity: AppCompatActivity) {
    companion object {

        const val LAUNCHER_TYPE_ALBUM = 0x01
        const val LAUNCHER_TYPE_CAMERA = 0x02
        const val LAUNCHER_TYPE_VIDEO = 0x03

        fun newInstance(activity: AppCompatActivity): ActivityResultLauncherUtils {
            return ActivityResultLauncherUtils(activity)
        }
    }

    private var mOnLauncherDataListener: OnLauncherDataListener? = null

    fun setOnLauncherDataListener(onLauncherDataListener: OnLauncherDataListener) {
        this.mOnLauncherDataListener = onLauncherDataListener
    }

    private val mLauncherCameraUri =
        mActivity.registerForActivityResult(TakeCameraUri())
        {
            mOnLauncherDataListener?.onLauncherForActivityResult(
                UriUtil.getFileAbsolutePath(
                    mActivity,
                    it
                ),
                LAUNCHER_TYPE_CAMERA
            )
        }

    private val mLauncherAlbum = mActivity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        mOnLauncherDataListener?.onLauncherForActivityResult(
            UriUtil.getFileAbsolutePath(mActivity, it),
            LAUNCHER_TYPE_ALBUM
        )
    }

    private val mActLauncherAlbum = mActivity.registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        val bitmap = PathUtil.voidToFirstBitmap(UriUtil.getFileAbsolutePath(mActivity, it))
        val firstUrl = PathUtil.bitmapToStringPath(mActivity, bitmap!!)
        val duration = PathUtil.getLocalVideoDuration(UriUtil.getFileAbsolutePath(mActivity, it))

        mOnLauncherDataListener?.onLauncherForActivityResult(
            UriUtil.getFileAbsolutePath(mActivity, it),
            firstUrl!!,
            duration,
            LAUNCHER_TYPE_VIDEO
        )
    }

    //选取视频文件（和选取相册类似）
    open fun launchVideoPick() {
        mActLauncherAlbum.launch("video/*")
    }

    //调用相册选择图片
    open fun launchAlbum() {
        mLauncherAlbum.launch("image/*")
    }

    //调用相机
    open fun launchCameraUri() {
        mLauncherCameraUri.launch(null)
    }

    interface OnLauncherDataListener {
        fun onLauncherForActivityResult(
            path: String = "",
            firstUrl: String = "",
            duration: Int = 0,
            type: Int
        ) {
            onLauncherForActivityResult(path, type)
        }

        fun onLauncherForActivityResult(
            path: String?,
            type: Int
        )
    }
}