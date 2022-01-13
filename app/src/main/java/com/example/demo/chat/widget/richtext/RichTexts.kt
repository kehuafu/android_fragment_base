package com.example.demo.chat.widget.richtext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import java.lang.NullPointerException


object RichTexts {
    private const val TAG = "RichText"
    fun setImageSpan(s: Spannable, info: TaggedInfo, context: Context, resourceId: Int) {
        removeSpans(s, info.start, info.end, ImageSpan::class.java)
        safelySetSpan(
            s,
            ImageSpan(context, resourceId, DynamicDrawableSpan.ALIGN_BASELINE),
            info.start,
            info.end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun setImageSpan(s: Spannable, info: TaggedInfo, span: DynamicDrawableSpan) {
        removeSpans(s, info, ImageSpan::class.java)
        safelySetSpan(s, span, info.start, info.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun setImageSpan(s: Spannable, start: Int, end: Int, context: Context, resourceId: Int) {
        removeSpans(s, start, end, ImageSpan::class.java)
        safelySetSpan(
            s,
            ImageSpan(context, resourceId, DynamicDrawableSpan.ALIGN_BASELINE),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun setRichTextClickSpan(s: Spannable, info: TaggedInfo, span: RichTextClickSpan) {
        removeSpans(s, info, RichTextClickSpan::class.java)
        safelySetSpan(s, span, info.start, info.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun safelySetSpan(s: Spannable, what: Any, start: Int, end: Int, flags: Int) {
        if (start in 0..end && end <= s.length) {
            s.setSpan(what, start, end, flags)
        } else {
            Log.e(
                TAG,
                "fail set spain,start:" + start + ",end:" + end + ",but lng is:" + s.length
            )
        }
    }

    fun <T> removeSpans(s: Spannable, info: TaggedInfo, clz: Class<T>?) {
        removeSpans(s, info.start, info.end, clz)
    }

    fun <T> removeSpans(s: Spannable, start: Int, end: Int, clz: Class<T>?) {
        val spans: Array<T> = s.getSpans(start, end, clz)
        if (spans != null) {
            for (span in spans) {
                try {
                    s.removeSpan(span)
                } catch (e: NullPointerException) {
                    Log.e(TAG, "remove spans error", e)
                }
            }
        }
    }

    class StickerSpan : DynamicDrawableSpan {
        private var mDrawable: Drawable

        constructor(context: Context, bitmap: Bitmap, maxWidth: Int, maxHeight: Int) : super(
            DynamicDrawableSpan.ALIGN_BASELINE
        ) {
            mDrawable = BitmapDrawable(context.resources, bitmap)
            val width: Int = mDrawable.intrinsicWidth
            val height: Int = mDrawable.intrinsicHeight
            mDrawable.setBounds(
                0,
                0,
                if (width > maxWidth) maxWidth else width,
                if (height > maxHeight) maxHeight else height
            )
        }

        constructor(context: Context, drawable: Int, maxWidth: Int, maxHeight: Int) : super(
            DynamicDrawableSpan.ALIGN_BASELINE
        ) {
            mDrawable = context.getResources().getDrawable(drawable)
            val width: Int = mDrawable.getIntrinsicWidth()
            val height: Int = mDrawable.getIntrinsicHeight()
            mDrawable.setBounds(
                0,
                0,
                if (width > maxWidth) maxWidth else width,
                if (height > maxHeight) maxHeight else height
            )
        }

        override fun draw(
            canvas: Canvas,
            text: CharSequence,
            start: Int,
            end: Int,
            x: Float,
            top: Int,
            y: Int,
            bottom: Int,
            paint: Paint
        ) {
            val b: Drawable = mDrawable
            canvas.save()
            canvas.translate(x, 0f)
            b.draw(canvas)
            canvas.restore()
        }

        override fun getDrawable(): Drawable {
            return mDrawable
        }
    }

    class RichTextClickSpan(
        private val listener: RichTextClickListener?,
        private val content: String
    ) :
        ClickableSpan() {
        override fun onClick(widget: View) {
            listener?.onRichTextClick(widget as TextView, content)
        }
    }

    /**
     * callback to be invoked when rich text is clicked
     */
    interface RichTextClickListener {
        fun onRichTextClick(v: TextView?, content: String?)
    }

    class TaggedInfo(var start: Int, var end: Int, var content: String)
}