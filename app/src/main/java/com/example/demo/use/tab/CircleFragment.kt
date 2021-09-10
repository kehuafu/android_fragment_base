package com.example.demo.use.tab

import android.os.Bundle
import com.example.demo.databinding.FragmentCircleBinding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment

class CircleFragment : BaseFragment<FragmentCircleBinding, MainViewModel, MainState>() {

    companion object {
        @JvmStatic
        fun newInstance(): CircleFragment {
            return CircleFragment()
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {

    }
}