package com.example.demo.chat.widget.richtext

import android.content.Context
import android.text.Spannable
import android.util.Log
import android.util.SparseArray
import android.widget.TextView
import com.example.demo.utils.EmojiManager
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.ktx.dp2pxInt
import java.util.regex.Matcher
import java.util.regex.Pattern

class ImageResolver : Resolver {
    override fun resolve(
        textView: TextView,
        sp: Spannable,
        extra: SparseArray<Any>,
        listener: RichTexts.RichTextClickListener
    ) {
        val matcher: Matcher = PATTERN.matcher(sp)
        val context: Context = textView.context
        while (matcher.find()) {
            try {
                val content: String = matcher.group()
                Log.e("ImageResolver", "resolve: $content")
                val info: RichTexts.TaggedInfo =
                    RichTexts.TaggedInfo(matcher.start(), matcher.end(), content)
                val span: RichTexts.RichTextClickSpan =
                    RichTexts.RichTextClickSpan(listener, info.content)
                sp.setSpan(span, info.start, info.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                val testSpan: RichTexts.StickerSpan =
                    RichTexts.StickerSpan(
                        context,
                        EmojiManager.emojiList[matcher.group()]!!,
                        textView.dp2pxInt(22f),
                        textView.dp2pxInt(22f)
                    )
                RichTexts.setImageSpan(sp, info, testSpan)
                textView.postInvalidate()
            } catch (e: Exception) {
                showToast("" + e.message)
            }
        }
    }

    companion object {
        //                const val IMG_MATCH_REGULAR = "\\[img](\\w+)\\[/img]"
        const val IMG_MATCH_REGULAR = "\\[(\\w+)\\]"
        var PATTERN: Pattern = Pattern.compile(IMG_MATCH_REGULAR)
    }
}