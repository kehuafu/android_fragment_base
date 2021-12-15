package com.example.demo.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.KeyboardUtils
import com.example.demo.R
import com.example.demo.databinding.FragmentChatBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment

class ChatFragment : BaseFragment<FragmentChatBinding, MainViewModel, MainState>() {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(savedInstanceState: Bundle?) {
        val arg = arguments?.getString("mid")
        viewBinding.nav.backIv.setOnClickListener {
            baseActivity.onBackPressed()
        }
        viewBinding.frameLayout.setOnClickListener {
            if (KeyboardUtils.isSoftInputVisible(requireActivity())) {
                KeyboardUtils.hideSoftInput(requireView())
            }
        }
        withViewBinding {
            nav.titleTv.text = "消息ID:$arg"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("@@", "ChatFragment--->onDestroy")
    }
}