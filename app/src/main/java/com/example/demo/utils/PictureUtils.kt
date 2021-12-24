package com.example.demo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.example.demo.app.App
import com.example.demo.chat.ChatActivity
import com.example.demo.chat.SecondActivity

class PictureUtils(private val context: Context) {

    companion object {
        const val RC_CAMERA_REQUEST_CODE = 0x011
        const val RC_CHOOSE_PHOTO_REQUEST_CODE = 0x012

        @JvmStatic
        fun build(context: Context): PictureUtils {
            return PictureUtils(context)
        }
    }

    private var mPhotoUri: Uri? = null

    class MyActivityResultContract : ActivityResultContract<String, String>() {
        override fun createIntent(context: Context, input: String?): Intent {
            val tt = build(context)
            return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            val data = intent?.getStringExtra("result")
            return if (resultCode == Activity.RESULT_OK && data != null) data
            else null
        }
    }

    /**
     * 调起相机
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun openCamera(): Intent {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //判断是否有相机
        if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) { //适配Android11
            val photoUri = createImageUri()
            if (photoUri != null) {
                mPhotoUri = photoUri
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                (App.appContext.applicationContext as AppCompatActivity).startActivityForResult(
//                    captureIntent,
//                    RC_CAMERA_REQUEST_CODE
//                )
            }
        }
        return captureIntent
    }

    /**
     * 调起系统相册,长按图片实现多选
     */
    fun selectAlbums() {
        Thread {
            runOnUiThread {
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_PICK
                intent.data =
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI //直接打开系统相册，不设置会有选择相册一步（例：系统相册、QQ浏览器相册）
                (App.appContext.applicationContext as AppCompatActivity).startActivityForResult(
                    Intent.createChooser(intent, "Select Picture"),
                    RC_CHOOSE_PHOTO_REQUEST_CODE
                )
            }
        }.start()
    }

    /**
     * 创建图片地址Uri，用于保存拍照后的照片
     */
    private fun createImageUri(): Uri? {
        val status = Environment.getExternalStorageState()
        //判断是否有SD卡，优先使用SD卡存储，当没有SD卡时使用手机储存
        return if (status.equals(Environment.MEDIA_MOUNTED)) {
            App.appContext.applicationContext.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )
        } else {
            App.appContext.applicationContext.contentResolver.insert(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                ContentValues()
            )
        }
    }

    /**
     * 选择从相册中选取图片
     * 单选
     */
    fun selectAlbum() {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        (App.appContext.applicationContext as AppCompatActivity).startActivityForResult(
            intent,
            RC_CHOOSE_PHOTO_REQUEST_CODE
        )
    }
}