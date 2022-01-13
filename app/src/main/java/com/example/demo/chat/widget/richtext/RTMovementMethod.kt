package com.example.demo.chat.widget.richtext

import android.text.Layout
import android.text.Selection
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.widget.TextView

class RTMovementMethod : LinkMovementMethod() {
    private var mLastTouchTime: Long = 0
    override fun onTouchEvent(
        widget: TextView, buffer: Spannable,
        event: MotionEvent
    ): Boolean {
        val action: Int = event.getAction()
        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN
        ) {
            var x = event.getX()
            var y = event.getY()
            x -= widget.getTotalPaddingLeft()
            y -= widget.getTotalPaddingTop()
            x += widget.getScrollX()
            y += widget.getScrollY()
            val layout: Layout = widget.getLayout()
            val line: Int = layout.getLineForVertical(y.toInt())
            val off: Int = layout.getOffsetForHorizontal(line, x)
            val link: Array<ClickableSpan> = buffer.getSpans(off, off, ClickableSpan::class.java)
            if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - mLastTouchTime < DELAY_TIME) {
                        link[0].onClick(widget)
                    }
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(
                        buffer,
                        buffer.getSpanStart(link[0]),
                        buffer.getSpanEnd(link[0])
                    )
                    mLastTouchTime = System.currentTimeMillis()
                }
                return true
            } else {
                Selection.removeSelection(buffer)
            }
        }
        return super.onTouchEvent(widget, buffer, event)
    }

    companion object {
        private const val DELAY_TIME = 500L
        fun getInstance(): MovementMethod? {
            if (sInstance == null) sInstance = RTMovementMethod()
            return sInstance
        }
        private var sInstance: LinkMovementMethod? = null
    }
}
