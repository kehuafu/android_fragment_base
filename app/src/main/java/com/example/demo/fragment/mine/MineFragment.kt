package com.example.demo.fragment.mine

import android.os.Bundle
import com.example.demo.databinding.FragmentMineBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
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