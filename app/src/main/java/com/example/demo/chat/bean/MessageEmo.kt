package com.example.demo.chat.bean

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.example.demo.R
import com.kehuafu.base.core.redux.IState

class MessageEmo(
    val id: Int,
    val title: String = "",
    val photoUrl: Drawable? = null,
) : IState {
    @DrawableRes
    fun toEmoDrawId(): Int {
        return when (title) {
            "[奸笑]" -> {
                R.drawable.wx00
            }
            "[嘿哈]" -> {
                R.drawable.wx01
            }
            "[捂脸]" -> {
                R.drawable.wx02
            }
            "[机智]" -> {
                R.drawable.wx03
            }
            "[耶]" -> {
                R.drawable.wx04
            }
            "[皱眉]" -> {
                R.drawable.wx05
            }
            "[加油]" -> {
                R.drawable.wx06
            }
            "[emm]" -> {
                R.drawable.wx07
            }
            "[加油加油]" -> {
                R.drawable.wx08
            }
            "[好的]" -> {
                R.drawable.wx09
            }
            "[天啊]" -> {
                R.drawable.wx10
            }
            "[打脸]" -> {
                R.drawable.wx11
            }
            "[社会社会]" -> {
                R.drawable.wx12
            }
            "[汗]" -> {
                R.drawable.wx13
            }
            "[强壮]" -> {
                R.drawable.wx14
            }
            "[鬼魂]" -> {
                R.drawable.wx15
            }
            "[吐舌]" -> {
                R.drawable.wx16
            }
            "[合十]" -> {
                R.drawable.wx17
            }
            "[礼物]" -> {
                R.drawable.wx18
            }
            "[庆祝]" -> {
                R.drawable.wx19
            }
            "[破涕为笑]" -> {
                R.drawable.wx20
            }
            "[笑脸]" -> {
                R.drawable.wx21
            }
            "[无语]" -> {
                R.drawable.wx22
            }
            "[失望]" -> {
                R.drawable.wx23
            }
            "[恐惧]" -> {
                R.drawable.wx24
            }
            "[脸红]" -> {
                R.drawable.wx25
            }
            "[感冒]" -> {
                R.drawable.wx26
            }
            "[吃瓜]" -> {
                R.drawable.wx27
            }
            "[哇]" -> {
                R.drawable.wx28
            }
            "[旺柴]" -> {
                R.drawable.wx29
            }
            "[瓢虫]" -> {
                R.drawable.wx30
            }
            "[企鹅]" -> {
                R.drawable.wx31
            }
            "[红包]" -> {
                R.drawable.wx32
            }
            "[发]" -> {
                R.drawable.wx33
            }
            "[福]" -> {
                R.drawable.wx34
            }
            else -> {
                R.drawable.ic_none_drawable
            }
        }
    }
}