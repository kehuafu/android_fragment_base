package com.example.demo.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import kotlin.math.ceil

class ImageResizeUtil {
    companion object {
        fun resize(path: String, w: Int, h: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options)

            //获取缩放比例
            options.inSampleSize = 1.coerceAtLeast(
                ceil(
                    (options.outWidth / w).coerceAtLeast(options.outHeight / h).toDouble()
                ).toInt()
            )

            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(path, options)
        }

        /**
         * 调整图片大小
         *
         * @param bitmap 源
         * @param dst_w  输出宽度
         * @param dst_h  输出高度
         * @return
         */
        fun imageScale(
            bitmap: Bitmap,
            dst_w: Int,
            dst_h: Int
        ): Bitmap? {
            val src_w = bitmap.width
            val src_h = bitmap.height
            val scale_w = dst_w.toFloat() / src_w
            val scale_h = dst_h.toFloat() / src_h
            val matrix = Matrix()
            matrix.postScale(scale_w, scale_h)
            return Bitmap.createBitmap(
                bitmap, 0, 0, src_w, src_h, matrix,
                true
            )
        }
    }
}