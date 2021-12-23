package com.example.demo.chat.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.example.demo.R
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatImageMsgBinding
import com.example.demo.databinding.LayItemChatTextMsgBinding
import com.kehuafu.base.core.ktx.loadImage
import com.example.demo.utils.DensityTool
import com.kehuafu.base.core.container.base.adapter.BaseListMultipleAdapter


/**
 * 测试列表适配器
 * 1.实体->T：Token
 * 2.视图->VB：LayItemTestBinding
 * 3.初始化->①设置状态监听，②属性委托对应的VB
 * 4.UI状态绑定->当数据的state变化时，对应的UI跟随状态改变
 */
class ChatListMultipleAdapter : BaseListMultipleAdapter<Message>() {

    private val mChatMsgAdapterTypeDelegate by lazy {
        ChatMsgAdapterTypeDelegate()
    }

    /**
     * 确定Item类型
     */
    override fun getItemViewType(position: Int): Int {
        return if (mItems.isEmpty()) {
            super.getItemViewType(position)
        } else {
            mChatMsgAdapterTypeDelegate.getItemViewType(mItems[position])
        }
    }

    /**
     * 初始化适配器
     */
    override fun init(parent: ViewGroup, viewType: Int): ViewBinding {
        setStateListener(this)
        return mChatMsgAdapterTypeDelegate.onCreateViewHolder(parent, viewType)
    }

    /**
     * UI状态绑定
     */
    @SuppressLint("SetTextI18n")
    override fun setState(item: Message, viewBinding: ViewBinding, position: Int) {
        when (item.messageType) {
            Message.MSG_TYPE_TEXT -> {
                setStateToTextMsg(viewBinding as LayItemChatTextMsgBinding, item, position)
            }
            Message.MSG_TYPE_IMAGE -> {
                setStateToImageMsg(viewBinding as LayItemChatImageMsgBinding, item, position)
            }
            Message.MSG_TYPE_SOUND -> {
            }
            Message.MSG_TYPE_VIDEO -> {
            }
            Message.MSG_TYPE_FILE -> {
            }
            Message.MSG_TYPE_LOCATION -> {
            }
            Message.MSG_TYPE_FACE -> {
            }
        }
    }

    private fun setStateToImageMsg(
        viewBinding: LayItemChatImageMsgBinding,
        item: Message,
        position: Int
    ) {
        viewBinding.leftMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.rightMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.leftMsgIv.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.rightMsgIv.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.ivSendFailed.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        if (item.messageSender) {
            viewBinding.layoutLeftText.visibility = View.GONE
            viewBinding.layoutRightText.visibility = View.VISIBLE
            if (item.loading || item.sendFailed) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(item.messageContent!!, options)
                DensityTool.setWH(
                    viewBinding.rightMsgIv,
                    (options.outWidth / 4.475f).toInt(),
                    (options.outHeight / 4.475f).toInt()
                )
                viewBinding.rightMsgIv.setImageBitmap(BitmapFactory.decodeFile(item.messageContent))
            } else {
                val height = item.v2TIMMessage.imageElem.imageList[2].height
                val width = item.v2TIMMessage.imageElem.imageList[2].width
                DensityTool.setWH(viewBinding.rightMsgIv, width, height)
                viewBinding.rightMsgIv.loadImage(item.messageContent!!)
            }
        } else {
            viewBinding.layoutLeftText.visibility = View.VISIBLE
            viewBinding.layoutRightText.visibility = View.GONE
            val height = item.v2TIMMessage.imageElem.imageList[2].height
            val width = item.v2TIMMessage.imageElem.imageList[2].width
            DensityTool.setWH(viewBinding.leftMsgIv, width, height)
            viewBinding.leftMsgIv.loadImage(item.v2TIMMessage.imageElem.imageList[2].url)
        }
        viewBinding.tvTime.isVisible = item.showTime!!
        viewBinding.tvTime.text = item.messageTime
        if (item.loading) {
            viewBinding.ivSendLoading.visibility = View.VISIBLE
            val mOperatingAnimCenter =
                AnimationUtils.loadAnimation(viewBinding.root.context, R.anim.loading_rotate_center)
            mOperatingAnimCenter.interpolator = LinearInterpolator()
            viewBinding.ivSendLoading.startAnimation(mOperatingAnimCenter)
        } else {
            viewBinding.ivSendLoading.visibility = View.GONE
        }
        viewBinding.ivSendFailed.isVisible = item.sendFailed
    }

    private fun setStateToTextMsg(
        viewBinding: LayItemChatTextMsgBinding,
        item: Message,
        position: Int
    ) {
        viewBinding.leftMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.rightMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.leftMsgText.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.rightMsgText.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.ivSendFailed.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        if (item.messageSender) {
            viewBinding.layoutLeftText.visibility = View.GONE
            viewBinding.layoutRightText.visibility = View.VISIBLE
            viewBinding.rightMsgText.text = item.messageContent
        } else {
            viewBinding.layoutLeftText.visibility = View.VISIBLE
            viewBinding.layoutRightText.visibility = View.GONE
            viewBinding.leftMsgText.text = item.messageContent
        }
        viewBinding.tvTime.isVisible = item.showTime!!
        viewBinding.tvTime.text = item.messageTime
        if (item.loading) {
            viewBinding.ivSendLoading.visibility = View.VISIBLE
            val mOperatingAnimCenter =
                AnimationUtils.loadAnimation(viewBinding.root.context, R.anim.loading_rotate_center)
            mOperatingAnimCenter.interpolator = LinearInterpolator()
            viewBinding.ivSendLoading.startAnimation(mOperatingAnimCenter)
        } else {
            viewBinding.ivSendLoading.visibility = View.GONE
        }
        viewBinding.ivSendFailed.isVisible = item.sendFailed
    }
}