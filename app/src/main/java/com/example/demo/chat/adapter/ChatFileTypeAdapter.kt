package com.example.demo.chat.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.databinding.LayItemChatMsgBinding
import com.example.demo.chat.bean.Message
import com.example.demo.chat.bean.MessageTheme
import com.example.demo.databinding.LayItemChatFileItemBinding
import com.kehuafu.base.core.container.base.adapter.BaseListAdapter
import com.kehuafu.base.core.ktx.loadImage
import com.kehuafu.base.core.ktx.viewBindings

/**
 * 测试列表适配器
 * 1.实体->T：Token
 * 2.视图->VB：LayItemTestBinding
 * 3.初始化->①设置状态监听，②属性委托对应的VB
 * 4.UI状态绑定->当数据的state变化时，对应的UI跟随状态改变
 */
class ChatFileTypeAdapter : BaseListAdapter<MessageTheme, LayItemChatFileItemBinding>() {

    /**
     * 初始化适配器
     */
    override fun init(parent: ViewGroup): LayItemChatFileItemBinding {
        setStateListener(this)
        val viewBinding by parent.viewBindings<LayItemChatFileItemBinding>(parent)
        return viewBinding
    }

    /**
     * UI状态绑定
     */
    @SuppressLint("SetTextI18n")
    override fun setState(
        item: MessageTheme,
        viewBinding: LayItemChatFileItemBinding,
        position: Int
    ) {
        viewBinding.ivTypeImage.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item, position)
        }
        viewBinding.tvTypeName.text = item.title
        viewBinding.ivTypeImage.loadImage(item.toThemeDrawId())
    }
}