package com.example.mvr.core.container.widget.toast

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.example.mvr.core.ktx.dp2px
import com.example.mvr.core.ktx.dp2pxInt

/**
 *
 * created by sai
 *
 * on 2019-08-23
 *
 * desc:  toast view
 *
 **/
class UToastContentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val titleTextView: TextView by lazy {
        val titleView = TextView(context)
        titleView.textSize = 14.0f
        //titleView.gravity = Gravity.CENTER
        titleView.setTextColor(Color.WHITE)
        val titleLayoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        //titleLayoutParams.gravity = Gravity.CENTER
        //titleLayoutParams.bottomMargin = dp2pxInt(12.0f)
        titleView.layoutParams = titleLayoutParams
        return@lazy titleView
    }

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        //minimumHeight = dp2pxInt(120.0f)
        minimumWidth = dp2pxInt(120.0f)
        setPadding(dp2pxInt(24.0f), dp2pxInt(12.0f), dp2pxInt(24.0f), dp2pxInt(12.0f))
        addView(titleTextView)
        setToastBg(-1)
        //setToastBg(R.drawable.rect_black_a70_corner_16)
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setTitleTextColor(color: Int) {
        titleTextView.setTextColor(color)
    }

    fun setTitleTextSize(textSize: Float) {
        titleTextView.textSize = textSize
    }

    fun setToastBg(@DrawableRes bgDrawable: Int) {
        if (bgDrawable == -1) {
            val shapeDrawable = PaintDrawable(Color.parseColor("#7f000000"))
            shapeDrawable.setCornerRadius(dp2px(26.0f))
            background = shapeDrawable
        } else {
            setBackgroundResource(bgDrawable)
        }
    }
}