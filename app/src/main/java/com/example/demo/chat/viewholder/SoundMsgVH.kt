package com.example.demo.chat.viewholder

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatSoundMsgBinding
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4

class SoundMsgVH(override val viewBinding: LayItemChatSoundMsgBinding) :
    BaseRecyclerViewAdapterV4.BaseViewHolder<Message>(
        viewBinding
    ) {
    override fun setState(item: Message, position: Int) {
        super.setState(item, position)
        setStateToSoundMsg(viewBinding, item, position = position)
    }

    @SuppressLint("SetTextI18n")
    private fun setStateToSoundMsg(
        viewBinding: LayItemChatSoundMsgBinding,
        item: Message,
        position: Int
    ) {
        viewBinding.leftMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.rightMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.ivSendFailed.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        if (item.messageSender) {
            viewBinding.layoutLeftSound.visibility = View.GONE
            viewBinding.layoutRightSound.visibility = View.VISIBLE
            viewBinding.rightMsgText.text = item.messageContent + "''"
            val anim = viewBinding.rightSoundIv.drawable as AnimationDrawable
            anim.start()
        } else {
            viewBinding.layoutLeftSound.visibility = View.VISIBLE
            viewBinding.layoutRightSound.visibility = View.GONE
            viewBinding.leftMsgText.text = item.messageContent + "''"
            val anim = viewBinding.leftSoundIv.drawable as AnimationDrawable
//            anim.start()
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
            viewBinding.ivSendLoading.clearAnimation()
        }
        viewBinding.ivSendFailed.isVisible = item.sendFailed
    }
}