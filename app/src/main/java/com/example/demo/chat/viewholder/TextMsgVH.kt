package com.example.demo.chat.viewholder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ConvertUtils.dp2px
import com.blankj.utilcode.util.LogUtils
import com.example.demo.R
import com.example.demo.chat.bean.Message
import com.example.demo.databinding.LayItemChatTextMsgBinding
import com.example.demo.utils.ImageResizeUtil
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV4
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.ktx.dp2px
import java.lang.reflect.Field
import android.R.attr.textSize
import java.util.regex.Matcher
import java.util.regex.Pattern


class TextMsgVH(override val viewBinding: LayItemChatTextMsgBinding) :
    BaseRecyclerViewAdapterV4.BaseViewHolder<Message>(
        viewBinding
    ) {
    override fun setState(item: Message, position: Int) {
        super.setState(item, position)
        setStateToTextMsg(viewBinding, item, position = position)
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
//        handleRichText(item.messageContent!!, viewBinding.root.context)
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

    private fun handleRichText(messageContent: String, context: Context): SpannableString? {
        try {
            if (messageContent.contains("wx02#哈哈哈wx02#")) {//wx02
                val index = messageContent.indexOfLast {
                    it == '#'
                }
                val emo =
                    messageContent.substring(index - 4, index)
                showToast("" + emo)
                return null
                //获取表情图片文件名
                val field: Field =
                    R.drawable::class.java.getDeclaredField(
                        "name"
                    )
                val resourceId = field.getInt(null)
                // 在android中要显示图片信息，必须使用Bitmap位图的对象来装载
                val bitmap: Bitmap =
                    BitmapFactory.decodeResource(context.resources, resourceId)
                //要让图片替代指定的文字用ImageSpan
                val imageSpan = ImageSpan(
                    viewBinding.root.context,
                    ImageResizeUtil.imageScale(bitmap, dp2px(22f), dp2px(22f))!!
                )
                val spannableString = SpannableString("name")
                spannableString.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                viewBinding.msgText.text = spannableString
            }
        } catch (e: Exception) {
            showToast("" + e.message)
        }
        return null
    }

//    fun disposeText(text: String): SpannableString {   //text:需要替换的句子
//        //使用SpannableString
//        val newText = SpannableString(text)
//        //匹配所有带有[]的词语
//        val pattern: Pattern = Pattern.compile("\\[.+?\\]")
//        val matcher: Matcher = pattern.matcher(text)
//        //循环匹配结果
//        while (matcher.find()) {
//            //如果emojiList含有，进行替换
//            if (EmojiDate.emojiList.get(matcher.group()) != null) {
//                //下面代码可以用更下面的那部分代码替换
//                val bitmap = BitmapFactory.decodeResource(
//                    context.getResources(),
//                    EmojiDate.emojiList.get(matcher.group())
//                )
//                //需要传递进文字的大小，更好的确定表情的大小
//                val scaleBitmap =
//                    Bitmap.createScaledBitmap(bitmap, textSize * 15 / 10, textSize * 15 / 10, true)
//                val span = ImageSpan(context, scaleBitmap)
//                newText.setSpan(
//                    span,
//                    matcher.start(),
//                    matcher.end(),
//                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
//                )
//            }
//        }
//        return newText
//    }

}