package com.example.demo.chat

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.example.demo.utils.GestureFrameLayout
import com.example.demo.utils.GestureFrameLayoutCallBack
import com.example.demo.databinding.FragmentChatBinding
import com.example.demo.databinding.LayChatInputViewBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.example.demo.utils.HeightProvider
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.widget.toast.showToast
import com.kehuafu.base.core.ktx.viewBindings

class ChatFragment : BaseFragment<FragmentChatBinding, MainViewModel, MainState>() {


    private var heightProvider: HeightProvider? = null

//    private val mLayChatInputViewBinding by viewBindings<LayChatInputViewBinding>() XXX

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onViewCreated(savedInstanceState: Bundle?) {
        val arg = arguments?.getString("mid")
        heightProvider = HeightProvider(requireActivity()).init()
        viewBinding.nav.backIv.setOnClickListener {
            baseActivity.onBackPressed()
        }
        viewBinding.frameLayout.setOnClickListener {
            if (heightProvider!!.isSoftInputVisible) {
                KeyboardUtils.hideSoftInput(requireView())
            }
        }

        withViewBinding {
            nav.titleTv.text = "消息ID:$arg"
        }
        heightProvider!!.setHeightListener {
            if (it.toFloat() > 0f) {
                startTranslateY(viewBinding.chatInputRl.root, -it.toFloat())
                return@setHeightListener
            }
            viewBinding.chatInputRl.root.translationY = -it.toFloat()
        }

        withViewBinding {
            chatInputRl.root.setOnTouchListener { v, event ->
                true
            }
            chatInputRl.ivVoice.setOnClickListener {
                if (chatInputRl.etMsg.isVisible) {
                    showToast("切换语音模式")

                } else {
                    showToast("切换文本模式")
                }
                chatInputRl.etMsg.isVisible = !chatInputRl.etMsg.isVisible
                chatInputRl.tvVoice.isVisible = !chatInputRl.etMsg.isVisible
            }
            chatInputRl.btnSendMsg.setOnClickListener {
                showToast("发送成功")
                withViewBinding {
                    chatInputRl.etMsg.text.clear()
                }
            }
            chatInputRl.etMsg.doAfterTextChanged {
                if (viewBinding.chatInputRl.etMsg.text.toString().trim().isBlank()) {
                    viewBinding.chatInputRl.btnSendMsg.visibility = View.GONE
                    viewBinding.chatInputRl.ivNavMore.visibility = View.VISIBLE
                } else {
                    viewBinding.chatInputRl.btnSendMsg.visibility = View.VISIBLE
                    viewBinding.chatInputRl.ivNavMore.visibility = View.GONE
                }
            }
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
    }

    /**
     * 软键盘的过渡动画效果
     */
    private fun startTranslateY(view: View, destinationY: Float) {
        val animatorSet = AnimatorSet()

        val currentY: Float = view.translationY
        val translateYAnimator =
            ObjectAnimator.ofFloat(view, "translationY", currentY, destinationY)

        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        animatorSet.play(translateYAnimator).after(alphaAnimator)
        animatorSet.duration = 80
        animatorSet.interpolator = AccelerateInterpolator()
        animatorSet.start()
    }


    override fun onPause() {
        super.onPause()
        KeyboardUtils.hideSoftInput(requireView())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("@@", "ChatFragment--->onDestroy")
    }
}