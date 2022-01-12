package com.example.demo.chat.viewholder

import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatEmoMsgBinding
import com.example.demo.databinding.LayItemChatEmptyMsgBinding
import com.example.demo.databinding.LayItemChatTextMsgBinding
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4

class EmoMsgVH(override val viewBinding: LayItemChatEmoMsgBinding) :
    BaseRecyclerViewAdapterV4.BaseViewHolder<Message>(
        viewBinding
    ) {
    override fun setState(item: Message, position: Int) {
        super.setState(item, position)
        setStateToTextMsg(viewBinding, item, position = position)
    }

    private fun setStateToTextMsg(
        viewBinding: LayItemChatEmoMsgBinding,
        item: Message,
        position: Int
    ) {
        viewBinding.leftMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.rightMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.msgText.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.ivSendFailed.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        if (item.messageSender) {
            viewBinding.leftMessageAvatar.visibility = View.GONE
            viewBinding.rightMessageAvatar.visibility = View.VISIBLE
            viewBinding.leftView.visibility = View.GONE
            viewBinding.rightView.visibility = View.VISIBLE
            viewBinding.llContent.gravity = Gravity.END
            viewBinding.msgText.setBackgroundResource(R.drawable.chat_msg_bg_radius_4)
        } else {
            viewBinding.leftMessageAvatar.visibility = View.VISIBLE
            viewBinding.rightMessageAvatar.visibility = View.GONE
            viewBinding.leftView.visibility = View.VISIBLE
            viewBinding.rightView.visibility = View.GONE
            viewBinding.llContent.gravity = Gravity.START
            viewBinding.msgText.setBackgroundResource(R.drawable.shape_radius_4)
        }
        viewBinding.msgText.text = item.messageContent
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