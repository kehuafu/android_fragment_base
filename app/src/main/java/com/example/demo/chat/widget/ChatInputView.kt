package com.example.demo.chat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ScreenUtils
import com.example.demo.R
import com.kehuafu.base.core.container.widget.toast.showToast


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
        const val KEY_BOARD_MODE_SOUND = 0x01
        const val KEY_BOARD_MODE_TEXT = 0x02
        const val KEY_BOARD_MODE_EXPRESSION = 0x03
        const val KEY_BOARD_MODE_FILE = 0x04
        var showKeyBoardMode = KEY_BOARD_MODE_TEXT

        @JvmStatic
        fun resetKeyBoardMode() {
            showKeyBoardMode = KEY_BOARD_MODE_TEXT
        }
    }

    private var mOnChatInputViewListener: OnChatInputViewListener? = null

    private var view = inflate(context, R.layout.lay_chat_input_view, this)

    init {
        etMsg().doAfterTextChanged {
            if (etMsg().text.toString().trim().isBlank()) {
                btnSendMsg().visibility = View.GONE
                ivNavMore().visibility = View.VISIBLE
            } else {
                btnSendMsg().visibility = View.VISIBLE
                ivNavMore().visibility = View.GONE
            }
            mOnChatInputViewListener?.onEtMsgContentChange(
                etMsg().text.toString()
            )
            mOnChatInputViewListener?.onEtMsgInputHeightChange(
                etMsg().height
            )
        }
        etMsg().setOnClickListener {
            showKeyBoardMode = KEY_BOARD_MODE_TEXT
            ivExpression().setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.expression_icon
                )
            )
        }

        ivNavMore().setOnClickListener {
            if (showKeyBoardMode == KEY_BOARD_MODE_FILE) {
                showKeyBoardMode = KEY_BOARD_MODE_TEXT
                mOnChatInputViewListener?.onPullUpList(true, showKeyBoardMode)
                etMsg().requestFocus()
            } else {
                showKeyBoardMode = KEY_BOARD_MODE_FILE
                etMsg().clearFocus()
                etMsg().isVisible = true
                tvVoice().isVisible = false
                ivVoice().setPadding(0, 0, 0, 0)
                ivVoice().setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.voice_icon
                    )
                )
                ivExpression().setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.expression_icon
                    )
                )
                mOnChatInputViewListener?.onShowEmo(false)
                mOnChatInputViewListener?.onPullUpList(
                    false,
                    showKeyBoardMode
                )
            }
        }

        ivExpression().setOnClickListener {
            if (showKeyBoardMode == KEY_BOARD_MODE_EXPRESSION) {
                showKeyBoardMode = KEY_BOARD_MODE_TEXT
                mOnChatInputViewListener?.onPullUpList(true, showKeyBoardMode)
                etMsg().requestFocus()
                ivExpression().setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.expression_icon
                    )
                )
            } else {
                showKeyBoardMode = KEY_BOARD_MODE_EXPRESSION
                ivExpression().setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.keyboard
                    )
                )
                ivVoice().setPadding(0, 0, 0, 0)
                ivVoice().setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.voice_icon
                    )
                )
                etMsg().isVisible = true
                tvVoice().isVisible = false
                etMsg().requestFocus()
                mOnChatInputViewListener?.onShowEmo(true)
                mOnChatInputViewListener?.onPullUpList(
                    false,
                    showKeyBoardMode
                )
            }

        }
        ivVoice().setOnClickListener {
            if (etMsg().isVisible) {
                PermissionUtils.permission(
                    PermissionConstants.MICROPHONE,
                    PermissionConstants.STORAGE
                )
                    .callback(object : PermissionUtils.FullCallback {
                        override fun onGranted(permissionsGranted: List<String>) {
                            showToast("同意授权")
                        }

                        override fun onDenied(
                            permissionsDeniedForever: List<String>,
                            permissionsDenied: List<String>
                        ) {
                            showToast("拒绝授权")
                            return
                        }
                    })
                    .theme { activity -> ScreenUtils.setFullScreen(activity) }
                    .request()
                showKeyBoardMode = KEY_BOARD_MODE_SOUND
                mOnChatInputViewListener?.onPullUpList(false, showKeyBoardMode)
                ivVoice().setPadding(6, 6, 6, 6)
                ivVoice().setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.keyboard
                    )
                )
                etMsg().clearFocus()
            } else {
                showKeyBoardMode = KEY_BOARD_MODE_TEXT
                mOnChatInputViewListener?.onPullUpList(true, showKeyBoardMode)
                ivVoice().setPadding(0, 0, 0, 0)
                ivVoice().setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.voice_icon
                    )
                )
            }
            etMsg().isVisible = !etMsg().isVisible
            tvVoice().isVisible = !etMsg().isVisible
            etMsg().requestFocus()
            ivExpression().setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.expression_icon
                )
            )
        }
        btnSendMsg().setOnClickListener {
            mOnChatInputViewListener?.onSendMsg(etMsg().text.toString().trim())
            etMsg().text.clear()
        }

        tvVoice().setOnTouchListener(OnTouchListener { v, event ->
            return@OnTouchListener mOnChatInputViewListener!!.onRecordVoice(v, event)
        })
    }

    fun resetEmoKeyBoard() {
        ivExpression().setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.expression_icon
            )
        )
    }

    fun setOnChatInputViewListener(onChatInputViewListener: OnChatInputViewListener) {
        this.mOnChatInputViewListener = onChatInputViewListener
    }

    fun etMsg(): EditText {
        return view.findViewById(R.id.et_msg)
    }

    fun ivNavMore(): ImageView {
        return view.findViewById(R.id.iv_nav_more)
    }

    fun tvVoice(): TextView {
        return view.findViewById(R.id.tv_voice)
    }

    fun ivVoice(): ImageView {
        return view.findViewById(R.id.iv_voice)
    }

    fun ivExpression(): ImageView {
        return view.findViewById(R.id.iv_expression)
    }

    fun btnSendMsg(): TextView {
        return view.findViewById(R.id.btn_send_msg)
    }

    interface OnChatInputViewListener {
        fun onRecordVoice(v: View, event: MotionEvent): Boolean
        fun onSendMsg(msg: String)
        fun onPullUpList(isShowSoftVisible: Boolean, showKeyBoardMode: Int)
        fun onShowEmo(show: Boolean)
        fun onEtMsgContentChange(context: String)
        fun onEtMsgInputHeightChange(height: Int)
    }
}

