package com.example.demo.chat.viewholder

import android.graphics.BitmapFactory
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.example.demo.R
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatVideoMsgBinding
import com.example.demo.utils.DensityTool
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4
import com.kehuafu.base.core.ktx.loadRoundImage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VideoMsgVH(override val viewBinding: LayItemChatVideoMsgBinding) :
    BaseRecyclerViewAdapterV4.BaseViewHolder<Message>(
        viewBinding
    ) {
    override fun setState(item: Message, position: Int) {
        super.setState(item, position)
        setStateToVideoMsg(viewBinding, item, position)
    }

    private fun setStateToVideoMsg(
        viewBinding: LayItemChatVideoMsgBinding,
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
            viewBinding.rightVideoDuration.text =
                item.secToTime(item.getMessageObject().videoElem.duration)
            val height =
                item.getMessageObject().videoElem.snapshotHeight / 8 * 2.5
            val width = item.getMessageObject().videoElem.snapshotWidth / 8 * 2.5
            if (height.toInt() != 0 && width.toInt() != 0) {
                DensityTool.setWH(viewBinding.msgVv, width.toInt(), height.toInt())
            } else {
                if (!item.loading) {
                    val opts = BitmapFactory.Options()
                    BitmapFactory.decodeFile(item.messageContent, opts)
                    val imageWidth: Int = (opts.outWidth / 8 * 2.5).toInt()
                    val imageHeight: Int = (opts.outHeight / 8 * 2.5.toInt())
                    Log.e("@@@@@@", "loadImage-messageSender-->${imageWidth}${imageHeight}")
                    DensityTool.setWH(viewBinding.msgVv, imageWidth, imageHeight)
                }
            }
            viewBinding.msgVv.loadRoundImage(item.messageContent, 10f)
        } else {
            viewBinding.llContent.gravity = Gravity.START
            viewBinding.leftMessageAvatar.visibility = View.VISIBLE
            viewBinding.rightMessageAvatar.visibility = View.GONE
            val height =
                item.getMessageObject().videoElem.snapshotHeight / 8 * 2.5
            val width = item.getMessageObject().videoElem.snapshotWidth / 8 * 2.5
            DensityTool.setWH(viewBinding.msgVv, width.toInt(), height.toInt())
            GlobalScope.launch {
                viewBinding.msgVv.loadRoundImage(item.getSnapshotUrl(), 10f)
            }
        }
        viewBinding.tvTime.isVisible = item.showTime!!
        viewBinding.tvTime.text = item.messageTime
        viewBinding.rightIvSendSuccess.isVisible = !item.loading
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