package com.example.demo.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    }
}