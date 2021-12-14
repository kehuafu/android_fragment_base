package com.example.demo.use.tab

import android.os.Bundle
import com.example.demo.databinding.FragmentMailListBinding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
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