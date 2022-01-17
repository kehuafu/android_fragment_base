package com.example.demo.chat.bean

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.example.demo.R
import com.example.demo.app.App
import com.kehuafu.base.core.redux.IState
import java.lang.reflect.Field

class MessageEmo(
    val id: Int,
    val title: String = "",
    val photoUrl: Drawable? = null,
) : IState {
    @DrawableRes
    fun toEmoDrawId(): Int {
        val stringMessageArrays =
            App.appContext.resources.getStringArray(R.array.chat_emoticon_type)
        stringMessageArrays.forEachIndexed { index, s ->
            if (s == title) {
                val drawableId = if (index <= 9) {
                    "0$index"
                } else {
                    index
                }
                //获取表情图片文件名
                val field: Field = R.drawable::class.java.getDeclaredField("wx$drawableId")
                return field.getInt(null)
            }
        }
        return -1
    }
}