package com.example.demo.fragment.mail

import android.os.Bundle
import com.example.demo.databinding.FragmentMailListBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment

class MailListFragment : BaseFragment<FragmentMailListBinding, MainViewModel, MainState>() {

    companion object {
        @JvmStatic
        fun newInstance(): MailListFragment {
            return MailListFragment()
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {

    }
}