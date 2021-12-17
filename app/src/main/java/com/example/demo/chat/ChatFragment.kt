package com.example.demo.chat

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.KeyboardUtils
import com.example.demo.R
import com.example.demo.chat.adapter.ChatListAdapter
import com.example.demo.databinding.FragmentChatBinding
import com.example.demo.fragment.message.bean.Message
import com.example.demo.fragment.message.mvvm.MessageViewModel
import com.example.demo.utils.HeightProvider
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.container.base.adapter.BaseRecyclerViewAdapterV2
import com.kehuafu.base.core.container.widget.toast.showToast

class ChatFragment :
    BaseFragment<FragmentChatBinding, MessageViewModel, MessageViewModel.MessageState>(),
    BaseRecyclerViewAdapterV2.OnItemClickListener<Message> {


    private var heightProvider: HeightProvider? = null

//    private val mLayChatInputViewBinding by viewBindings<LayChatInputViewBinding>() XXX

    private var mChatListAdapter = ChatListAdapter()

    private var mChatMsgList = mutableListOf<Message>()

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
        heightProvider!!.setHeightListener {
            if (it.toFloat() > 0f) {
                viewBinding.chatRv.stopScroll()
                startTranslateY(viewBinding.chatInputRl.root, -it.toFloat())
                viewBinding.chatRv.scrollToPosition(0)
                viewBinding.frameLayout.translationY = -it.toFloat()
                viewBinding.chatInputRl.etMsg.requestFocus()
                return@setHeightListener
            }
            viewBinding.chatInputRl.root.translationY = -it.toFloat()
            viewBinding.frameLayout.translationY = -it.toFloat()
        }

        withViewBinding {
            nav.titleTv.text = "消息ID:$arg"
            chatInputRl.root.setOnTouchListener { v, event ->
                true
            }
            chatInputRl.ivVoice.setOnClickListener {
                if (chatInputRl.etMsg.isVisible) {
                    showToast("切换语音模式")
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(requireView())
                    }
                    chatInputRl.ivVoice.setPadding(6, 6, 6, 6)
                    chatInputRl.ivVoice.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.keyboard
                        )
                    )
                } else {
                    showToast("切换文本模式")
                    if (!heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.showSoftInput(requireView())
                    }
                    chatInputRl.ivVoice.setPadding(0, 0, 0, 0)
                    chatInputRl.ivVoice.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.voice_icon
                        )
                    )
                }
                chatInputRl.etMsg.isVisible = !chatInputRl.etMsg.isVisible
                chatInputRl.tvVoice.isVisible = !chatInputRl.etMsg.isVisible
            }
            chatInputRl.ivExpression.setOnClickListener {
                showToast("显示我的表情包")
                chatInputRl.ivExpression.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.keyboard
                    )
                )
            }
            chatInputRl.btnSendMsg.setOnClickListener {
                showToast("发送成功")
                withViewBinding {
                    viewModel.sendMsg(chatInputRl.etMsg.text.toString(), mChatMsgList)
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
            mChatListAdapter.setOnItemClickListener(this@ChatFragment)
            viewBinding.chatRv.adapter = mChatListAdapter
            chatRv.itemAnimator = null
            chatRv.layoutManager = LinearLayoutManager(baseActivity)
            (chatRv.layoutManager as LinearLayoutManager).reverseLayout = true//列表翻转
            chatRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    Log.e("@@", "滑动")
                    if (heightProvider!!.isSoftInputVisible) {
                        KeyboardUtils.hideSoftInput(requireView())
                    }
                }
            })
        }
    }

    override fun onLoadDataSource() {
        super.onLoadDataSource()
        viewModel.getMessage("123456")
    }

    override fun onStateChanged(state: MessageViewModel.MessageState) {
        super.onStateChanged(state)
        mChatListAdapter.resetItems(state.messageList)
        Log.d("@@", "有新消息！" + state.messageList.size)
        mChatMsgList = state.messageList
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
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.start()
    }


    override fun onPause() {
        super.onPause()
        KeyboardUtils.hideSoftInput(requireView())
        if (heightProvider!!.isSoftInputVisible) {
            KeyboardUtils.hideSoftInput(requireView())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (heightProvider!!.isSoftInputVisible) {
            KeyboardUtils.hideSoftInput(requireView())
        }
    }

    override fun onItemClick(itemView: View, item: Message, position: Int?) {
        when (itemView.id) {
            R.id.left_message_avatar, R.id.right_message_avatar -> {
                showToast("头像")
            }
            R.id.left_msg_text, R.id.right_msg_text -> {
                showToast("文本")
            }
            else -> {
                if (heightProvider!!.isSoftInputVisible) {
                    KeyboardUtils.hideSoftInput(requireView())
                }
            }
        }
    }
}