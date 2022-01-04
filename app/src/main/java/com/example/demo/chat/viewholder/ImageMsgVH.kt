package com.example.demo.chat.viewholder

import android.graphics.BitmapFactory
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatImageMsgBinding
import com.example.demo.databinding.LayItemChatTextMsgBinding
import com.example.demo.utils.DensityTool
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4
import com.kehuafu.base.core.ktx.loadImage
import com.kehuafu.base.core.ktx.loadRoundImage

class ImageMsgVH(override val viewBinding: LayItemChatImageMsgBinding) :
    BaseRecyclerViewAdapterV4.BaseViewHolder<Message>(
        viewBinding
    ) {
    override fun setState(item: Message, position: Int) {
        super.setState(item, position)
        setStateToImageMsg(viewBinding, item, position)
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
        viewBinding.msgVv.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.ivSendFailed.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        if (item.messageSender) {
            viewBinding.llContent.gravity = Gravity.END
            viewBinding.leftMessageAvatar.visibility = View.GONE
            viewBinding.rightMessageAvatar.visibility = View.VISIBLE
            if (!item.loading) {
                val height = item.v2TIMMessage.imageElem.imageList[2].height
                val width = item.v2TIMMessage.imageElem.imageList[2].width
                DensityTool.setWH(viewBinding.msgVv, width, height)
            }

        } else {
            viewBinding.llContent.gravity = Gravity.START
            viewBinding.leftMessageAvatar.visibility = View.VISIBLE
            viewBinding.rightMessageAvatar.visibility = View.GONE
            val height = item.v2TIMMessage.imageElem.imageList[2].height
            val width = item.v2TIMMessage.imageElem.imageList[2].width
            DensityTool.setWH(viewBinding.msgVv, width, height)
        }
        viewBinding.msgVv.loadRoundImage(item.messageContent!!, 10F)
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
            viewBinding.ivSendLoading.clearAnimation()
        }
        viewBinding.ivSendFailed.isVisible = item.sendFailed
    }

}