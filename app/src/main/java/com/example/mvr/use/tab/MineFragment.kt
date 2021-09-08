package com.example.mvr.use.tab

import android.os.Bundle
import com.example.mvr.core.container.base.BaseFragment
import com.example.mvr.databinding.FragmentMineBinding
import com.example.mvr.use.mvr.MainState
import com.example.mvr.use.mvr.MainViewModel

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