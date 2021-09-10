package com.example.demo.use.tab

import android.os.Bundle
import com.example.demo.databinding.FragmentMineBinding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment

class MineFragment : BaseFragment<FragmentMineBinding, MainViewModel, MainState>() {

    companion object {
        @JvmStatic
        fun newInstance(): MineFragment {
            return MineFragment()
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {

    }
}