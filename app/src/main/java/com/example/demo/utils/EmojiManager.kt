package com.example.demo.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ConvertUtils
import com.example.demo.R
import com.example.demo.app.App
import java.lang.reflect.Field
import java.util.regex.Matcher
import java.util.regex.Pattern

import com.example.demo.chat.widget.richtext.ImageResolver

import com.example.demo.chat.widget.richtext.RichTextWrapper
import com.example.demo.chat.widget.richtext.RichTexts.RichTextClickListener
import com.kehuafu.base.core.ktx.runOnMainThread
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.log


object EmojiManager {

    var emojiList: HashMap<String, Int> = HashMap()
    private const val TAG = "EmojiManager"

    fun initLocalEmojiDate() {
        val stringMessageArrays =
            App.appContext.resources.getStringArray(R.array.chat_emoticon_type)
        stringMessageArrays.forEachIndexed { index, s ->
            val drawableId = if (index <= 9) {
                "0$index"
            } else {
                index
            }
            //获取表情图片文件名
            val field: Field = R.drawable::class.java.getDeclaredField("wx$drawableId")
            val resourceId = field.getInt(null)
            emojiList[s] = resourceId
        }
    }

    fun removeEMO(editText: EditText, context: Context) {
        val pattern: Pattern = Pattern.compile("\\[(.+?)\\]")
        val index = editText.selectionStart//光标位置
        if (!editText.text.contains(pattern.toRegex())) {
            editText.text.delete(
                index - 1,
                index
            )
            return
        }
        val matcherLastLeftIndex = editText.text.toString().substring(0, index).lastIndexOf("[")
        Log.e("EmojiManager", "index: $index")
        Log.e("EmojiManager", "matcherLastLeftIndex: $matcherLastLeftIndex")
        val text: String = if (matcherLastLeftIndex == -1) {
            editText.text.toString().substring(0, index)
        } else {
            editText.text.toString().substring(matcherLastLeftIndex, index)
        }
        Log.e(TAG, "removeEMO: $text")
        val matcher: Matcher = pattern.matcher(text)
        //匹配结果
        if (matcher.find()) {
            //如果emojiList含有，进行替换
            val matcherLastRightIndex = text.lastIndexOf("]")
            Log.e("EmojiManager", "matcherLastRightIndex: $matcherLastRightIndex")
            if (emojiList[matcher.group()] != null && matcherLastRightIndex == text.length - 1) {
                val start = matcher.start()
                val end = matcher.end()
                editText.text.delete(
                    index - (end - start),
                    index
                )
            } else {
                editText.text.delete(
                    index - 1,
                    index
                )
            }
        } else {
            editText.text.delete(
                index - 1,
                index
            )
        }
    }

    fun disposeText(
        textString: String,
        context: Context,
        lineHeight: Int
    ): SpannableString? {
        val pattern: Pattern = Pattern.compile("\\[(.+?)\\]")
        if (!textString.contains(pattern.toRegex())) {
            return null
        }
        Log.e("EmojiManager", "disposeText: $textString")
        //使用SpannableString
        val newText = SpannableString(textString)
        //匹配所有带有[]的词语
        val matcher: Matcher = pattern.matcher(textString)

        //循环匹配结果
        while (matcher.find()) {
            //如果emojiList含有，进行替换
            if (emojiList[matcher.group()] != null) {
                //下面代码可以用更下面的那部分代码替换
//                val bitmap = BitmapFactory.decodeResource(
//                    context.resources,
//                    emojiList[matcher.group()]!!
//                )
//                val bitmapDrawable = BitmapDrawable(context.resources, bitmap)
//                bitmapDrawable.setBounds(0, 0, lineHeight, lineHeight)
                val fuDrawable: Drawable =
                    ContextCompat.getDrawable(context, emojiList[matcher.group()]!!)!!
                fuDrawable.setBounds(0, 0, lineHeight, lineHeight)
                //需要传递进文字的大小，更好的确定表情的大小
                val imageSpan = ImageSpan(
                    fuDrawable
                )
                val start = matcher.start()
                val end = matcher.end()
                newText.setSpan(
                    imageSpan,
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }
        return newText
    }
}