package com.example.demo.chat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.viewbinding.ViewBinding
import com.example.demo.R
import com.example.demo.databinding.LayChatInputViewBinding
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.ktx.viewBindings


/**
 *  desc:  聊天软键盘输入框
 */
@SuppressLint("ClickableViewAccessibility")
class ChatInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = ChatInputView::class.java.simpleName
    }

    private var mOnChatInputViewListener: OnChatInputViewListener? = null

    private val viewBinding by viewBindings<LayChatInputViewBinding>()

    init {
        View.inflate(context, R.layout.lay_chat_input_view, this)
    }

    fun etMsg(): EditText {
        return viewBinding.etMsg
    }

    fun ivNavMore(): ImageView {
        return viewBinding.ivNavMore
    }

    fun tvVoice(): TextView {
        return viewBinding.tvVoice
    }

    fun ivVoice(): ImageView {
        return viewBinding.ivVoice
    }

    fun ivExpression(): ImageView {
        return viewBinding.ivExpression
    }

    fun btnSendMsg(): TextView {
        return viewBinding.btnSendMsg
    }


    interface OnChatInputViewListener {
        fun onRecordVoice(v: View, event: MotionEvent): Boolean
        fun onClickVoice(v: View)
        fun onSendMsg(msg: String)
    }
}

