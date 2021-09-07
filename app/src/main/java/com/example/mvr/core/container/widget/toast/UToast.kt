package com.example.mvr.core.container.widget.toast

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.example.mvr.core.ktx.runOnMainThread

/**
 * Created by jzz
 *
 * on 2017/9/22
 *
 * desc:toast
 */
class UToast {}

@Volatile
internal var mToastResult: Toast? = null

@Volatile
internal lateinit var mToastContext: Application

fun setup(context: Context) {
    mToastContext = context as Application
}

@SuppressLint("InflateParams")
internal fun makeToast(): Toast {
    // Log.e("toast", "makeToast toastResult=$toastResult")
    if (mToastResult == null) {
        mToastResult = Toast(mToastContext)
    }
    return mToastResult!!
}

internal fun cancelToast() {
    // Log.e("toast", "cancelToast toastResult=$toastResult")
    mToastResult?.cancel()
    mToastResult = null //解决 Android 10.0 同一个 toast 实例，无法一直调用show一直显示 toast 的缺陷，10.0
}

@JvmOverloads
fun showToast(@StringRes rid: Int, @DrawableRes drawableId: Int = -1) {
    if (drawableId != -1) {
        showToast(
            mToastContext.resources.getString(rid),
            ResourcesCompat.getDrawable(mToastContext.resources, drawableId, mToastContext.theme)
        )
    } else {
        showToast(mToastContext.resources.getString(rid), null)
    }
}

@JvmOverloads
fun showToast(
    msg: String,
    icon: Drawable? = null,
    duration: Int = Toast.LENGTH_SHORT,
    gravity: Int = Gravity.CENTER,
    yOffset: Int = 0
) {
    runOnMainThread({
        cancelToast()
        makeToast().apply {
            initToastContentView(msg, icon)
            if (duration != this.duration) {
                this.duration = duration
            }
            if (yOffset != this.yOffset || gravity != this.gravity) {
                setGravity(
                    gravity, 0, if (gravity == Gravity.BOTTOM) {
                        if (yOffset == 0) {
                            val identifier = mToastContext.resources.getIdentifier(
                                "toast_y_offset",
                                "dimen",
                                "android"
                            )
                            val dimensionPixelOffset =
                                mToastContext.resources.getDimensionPixelOffset(identifier)
                            dimensionPixelOffset
                        } else {
                            yOffset
                        }
                    } else {
                        0
                    }
                )
            }
        }.show()
    })
}

private fun Toast.initToastContentView(msg: String, icon: Drawable?) {
    if (view == null) {
        view = UToastContentView(mToastContext)
    }
    val uToastView = view as? UToastContentView
    uToastView?.setTitle(msg)
}
