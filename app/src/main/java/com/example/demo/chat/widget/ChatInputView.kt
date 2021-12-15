//package com.example.demo.chat.widget
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.text.InputType
//import android.util.AttributeSet
//import android.view.KeyEvent
//import android.view.MotionEvent
//import android.view.View
//import android.view.inputmethod.EditorInfo
//import android.widget.EditText
//import android.widget.FrameLayout
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.core.widget.doAfterTextChanged
//import com.blankj.utilcode.util.KeyboardUtils
//import com.example.demo.R
//import com.example.demo.databinding.LayChatInputViewBinding
//import com.kehuafu.base.core.ktx.viewBindings
//import com.purple.square.R
//import com.purple.square.databinding.LayChatInputViewBinding
//import com.uni7corn.lib.base.container.viewBindings
//
//
///**
// *  desc:  聊天软键盘输入框
// */
//@SuppressLint("ClickableViewAccessibility")
//class ChatInputView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) :
//    FrameLayout(context, attrs, defStyleAttr), TextView.OnEditorActionListener {
//
//    companion object {
//        private val TAG = ChatInputView::class.java.simpleName
//    }
//
//    private var mOnChatInputViewListener: OnChatInputViewListener? = null
//    private val viewBinding by viewBindings<LayChatInputViewBinding>()
//
//    init {
//        View.inflate(context, R.layout.lay_chat_input_view, this)
//        viewBinding.ivVoice.setOnClickListener {
//            if (it.tag == null) {
//                it.tag = true
//                showAudioButton()
//                mOnChatInputViewListener?.onClickVoice(it)
//                KeyboardUtils.hideSoftInput(etInputView())
//            } else {
//                it.tag = null
//                hideAudioButton()
//                KeyboardUtils.showSoftInput(etInputView())
//            }
//        }
//
//
//        viewBinding.etMsg.imeOptions = EditorInfo.IME_ACTION_SEND
//        viewBinding.etMsg.setOnEditorActionListener(this)
//        viewBinding.etMsg.inputType =
//            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
//
//        viewBinding.etMsg.doAfterTextChanged {
//            if (viewBinding.etMsg.text.toString().trim().isBlank()) {
//                viewBinding.btnSendMsg.visibility = View.GONE
//                viewBinding.ivNavMore.visibility = View.VISIBLE
//            } else {
//                viewBinding.btnSendMsg.visibility = View.VISIBLE
//                viewBinding.ivNavMore.visibility = View.GONE
//            }
//        }
//
//        viewBinding.tvRecordVoice.setOnTouchListener { v, event ->
//            return@setOnTouchListener mOnChatInputViewListener?.onRecordVoice(v, event) ?: false
//        }
////        iv_face.setOnClickListener {
////            mOnChatInputViewListener?.onFacePanelClick()
////        }
////        iv_more.setOnClickListener {
////            mOnChatInputViewListener?.onMorePanelClick()
////        }
//        viewBinding.btnSendMsg.setOnClickListener {
//            val inputMsg = viewBinding.etMsg.text.toString().trim()
//            viewBinding.etMsg.text = null
//            mOnChatInputViewListener?.onSendMsg(inputMsg)
//        }
//    }
//
//    fun etInputView(): EditText {
//        return viewBinding.etMsg
//    }
//
//    fun getIvEmoji(): ImageView {
//        return viewBinding.ivFace
//    }
//
//    fun getIvMore(): ImageView {
//        return viewBinding.ivNavMore
//    }
//
//    fun getIvVoice(): ImageView {
//        return viewBinding.ivVoice
//    }
//
//    fun setOnChatInputViewListener(onChatInputViewListener: OnChatInputViewListener) {
//        this.mOnChatInputViewListener = onChatInputViewListener
//    }
//
//    private fun showAudioButton() {
//        viewBinding.tvRecordVoice.visibility = View.VISIBLE
//        viewBinding.ivVoice.setImageResource(R.drawable.ic_chat_text_keyboard)
//    }
//
//    fun hideAudioButton() {
//        viewBinding.tvRecordVoice.visibility = View.GONE
//        viewBinding.ivVoice.setImageResource(R.drawable.ic_chat_voice)
//        viewBinding.ivVoice.tag = null
//    }
//
//    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
//        when (actionId) {
//            EditorInfo.IME_ACTION_SEND -> {
//                mOnChatInputViewListener?.onSendMsg(v?.text.toString().trim())
//                viewBinding.etMsg.setText("")
//            }
//        }
//        return true
//    }
//
//    interface OnChatInputViewListener {
//        fun onRecordVoice(v: View, event: MotionEvent): Boolean
//        fun onClickVoice(v: View)
//        fun onSendMsg(msg: String)
//    }
//}
//
