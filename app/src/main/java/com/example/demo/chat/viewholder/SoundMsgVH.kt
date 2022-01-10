package com.example.demo.chat.viewholder

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatSoundMsgBinding
import com.example.demo.utils.AudioRecodeUtils
import com.example.demo.utils.MediaPlayerManager
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SoundMsgVH(override val viewBinding: LayItemChatSoundMsgBinding) :
    BaseRecyclerViewAdapterV4.BaseViewHolder<Message>(
        viewBinding
    ) {
    override fun setState(item: Message, position: Int) {
        super.setState(item, position)
        setStateToSoundMsg(viewBinding, item, position = position)
    }

    private lateinit var audioRecodeUtils: AudioRecodeUtils

    @SuppressLint("SetTextI18n")
    private fun setStateToSoundMsg(
        viewBinding: LayItemChatSoundMsgBinding,
        item: Message,
        position: Int
    ) {
        val anim: AnimationDrawable
        if (item.messageSender) {
            viewBinding.layoutLeftSound.visibility = View.GONE
            viewBinding.layoutRightSound.visibility = View.VISIBLE
            viewBinding.rightMsgText.text = item.messageContent + "''"
            anim = viewBinding.rightSoundIv.drawable as AnimationDrawable
        } else {
            viewBinding.layoutLeftSound.visibility = View.VISIBLE
            viewBinding.layoutRightSound.visibility = View.GONE
            viewBinding.leftMsgText.text = item.messageContent + "''"
            anim = viewBinding.leftSoundIv.drawable as AnimationDrawable
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
        viewBinding.leftMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.rightMessageAvatar.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.ivSendFailed.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, item = item, position)
        }
        viewBinding.leftMessageLl.setOnClickListener {
            GlobalScope.launch {
                audioRecodeUtils.playRecord(item.getSoundUrl(), anim)
            }
        }
        viewBinding.rightMessageLl.setOnClickListener {
            GlobalScope.launch {
                audioRecodeUtils.playRecord(item.getSoundUrl(), anim)
            }
        }
        audioRecodeUtils = AudioRecodeUtils()
        audioRecodeUtils.setOnAudioPlayStatusListener { playState, anim ->
            when (playState) {
                MediaPlayerManager.PLAY_STATE_PLAYING -> {
                    anim.start()
                }
                MediaPlayerManager.PLAY_STATE_FINISHED -> {
                    if (anim.isRunning) {
                        anim.stop()
                        anim.selectDrawable(0)
                    }
                }
                MediaPlayerManager.PLAY_STATE_STOP -> {
                    if (anim.isRunning) {
                        anim.stop()
                        anim.selectDrawable(0)
                    }
                    Log.e("sss---->", "停止$anim")
                }
            }
        }
    }
}