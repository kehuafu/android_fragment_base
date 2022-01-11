package com.example.demo.chat.bean

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.example.demo.R
import com.example.demo.app.App
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.redux.IState
import com.tencent.imsdk.v2.V2TIMMessage
import java.text.FieldPosition

class MessageEmo(
    val id: Int,
    val title: String = "",
    val photoUrl: Drawable? = null,
) : IState {
    @DrawableRes
    fun toEmoDrawId(): Int {
        return when (title) {
            "/奸笑" -> {
                R.drawable.wx003
            }
            "/嘿哈" -> {
                R.drawable.wx004
            }
            "/捂脸" -> {
                R.drawable.wx005
            }
            "/机智" -> {
                R.drawable.wx006
            }
            "/耶" -> {
                R.drawable.wx008
            }
            "/皱眉" -> {
                R.drawable.wx009
            }
            "/加油" -> {
                R.drawable.wx011
            }
            "/emm" -> {
                R.drawable.wx012
            }
            "/加油加油" -> {
                R.drawable.wx014
            }
            "/好的" -> {
                R.drawable.wx015
            }
            "/天啊" -> {
                R.drawable.wx016
            }
            "/打脸" -> {
                R.drawable.wx017
            }
            "/社会社会" -> {
                R.drawable.wx018
            }
            "/汗" -> {
                R.drawable.wx019
            }
            "/强壮" -> {
                R.drawable.wx020
            }
            "/鬼魂" -> {
                R.drawable.wx021
            }
            "/吐舌" -> {
                R.drawable.wx022
            }
            "/合十" -> {
                R.drawable.wx023
            }
            "/礼物" -> {
                R.drawable.wx024
            }
            "/庆祝" -> {
                R.drawable.wx025
            }
            "/破涕为笑" -> {
                R.drawable.wx026
            }
            "/笑脸" -> {
                R.drawable.wx027
            }
            "/无语" -> {
                R.drawable.wx028
            }
            "/失望" -> {
                R.drawable.wx029
            }
            "/恐惧" -> {
                R.drawable.wx030
            }
            "/脸红" -> {
                R.drawable.wx031
            }
            "/感冒" -> {
                R.drawable.wx032
            }
            "/吃瓜" -> {
                R.drawable.wx033
            }
            "/哇" -> {
                R.drawable.wx034
            }
            "/旺柴" -> {
                R.drawable.wx035
            }
            "/瓢虫" -> {
                R.drawable.wx109
            }
            "/企鹅" -> {
                R.drawable.wx132
            }
            "/红包" -> {
                R.drawable.wx007
            }
            "/发" -> {
                R.drawable.wx002
            }
            "/福" -> {
                R.drawable.wx001
            }
            else -> {
                R.drawable.ic_none_drawable
            }
        }
    }
}