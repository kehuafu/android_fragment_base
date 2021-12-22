package com.kehuafu.base.core.ktx

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


/**
 * 拓展函数  可以更加方便的使用并加载图片   用法为 kotlin: ImageView().loadImage()
 *                                             java:  ImageLoaderKt.loadImage()
 *
 * @receiver ImageView
 * @param drawableId Int
 * @param placeHolderId Int
 * @param errorId Int
 * @param isCenterCrop Boolean
 */
@JvmOverloads
fun ImageView.loadImage(@DrawableRes drawableId: Int = -1, @DrawableRes placeHolderId: Int = -1, @DrawableRes errorId: Int = -1, isCenterCrop: Boolean = false) {
    var options = RequestOptions.placeholderOf(placeHolderId).error(errorId)

    if (isCenterCrop) {
        options = options.centerCrop()
    }
    Glide.with(this).load(drawableId).apply(options).into(this)
}

/**
 * 拓展函数  可以更加方便的使用并加载图片   用法为 kotlin: ImageView().loadImage()
 *                                             java:  ImageLoaderKt.loadImage()
 * @receiver ImageView
 * @param url String
 * @param placeHolderId Int
 * @param errorId Int
 * @param isCenterCrop Boolean
 */
@JvmOverloads
fun ImageView.loadImage(url: String?, @DrawableRes placeHolderId: Int = -1, @DrawableRes errorId: Int = -1, isCenterCrop: Boolean = false) {
    var options = RequestOptions.placeholderOf(placeHolderId).error(errorId)
    if (isCenterCrop) {
        options = options.centerCrop()
    }
    Glide.with(this).load(url).apply(options).into(this)
}

/**
 * 加载圆角图片
 *
 * @receiver ImageView
 * @param url String?
 * @param topLeft Float
 * @param topRight Float
 * @param bottomLeft Float
 * @param bottomRight Float
 */
fun ImageView.loadRoundImage(url: String?, topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
    val requestOptions = RequestOptions.noTransformation().transform(CenterCrop(), GranularRoundedCorners(topLeft, topRight, bottomLeft, bottomRight))
    Glide.with(this).asDrawable().load(url).apply(requestOptions).into(this)
}

fun ImageView.loadRoundImage(url: String?, round: Float) {
    loadRoundImage(url, round, round, round, round)
}

@Suppress("DEPRECATION")
fun View.loadBgImage(url: String?) {
    Glide.with(this).asDrawable().load(url ?: "").into(object : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            background = resource
        }
    })
}


