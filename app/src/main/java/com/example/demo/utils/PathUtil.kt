package com.example.demo.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.blankj.utilcode.util.PathUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

object PathUtil {

    /**
     * 获取视频首帧图并转化为bitmap
     * @param videoUrl
     * @return
     */
    fun voidToFirstBitmap(videoUrl: String): Bitmap? {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(videoUrl)
        return metadataRetriever.frameAtTime
    }

    /**
     * 将bitmap转化成本地图片路径
     * @param context
     * @param bitmap
     * @return
     */
    fun bitmapToStringPath(context: Context, bitmap: Bitmap): String? {
        val filePic: File
        val savePath: String = PathUtils.getExternalAppCachePath()
        try {
            filePic = File(savePath + UUID.randomUUID().toString() + ".jpg")
            if (!filePic.exists()) {
                filePic.parentFile.mkdirs()
                filePic.createNewFile()
            }
            val fos = FileOutputStream(filePic)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return filePic.absolutePath
    }

    /**
     * get Local video duration
     *
     * @return
     */
    fun getLocalVideoDuration(videoPath: String?): Int {
        //除以 1000 返回是秒
        val duration: Int
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(videoPath)
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                .toInt() / 1000

            //时长(毫秒)
            //String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            //宽
            val width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            //高
            val height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
        return duration
    }
}