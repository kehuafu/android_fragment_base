package com.example.mvr.use.tab

import android.os.Bundle
import com.example.mvr.core.container.base.BaseFragment
import com.example.mvr.databinding.FragmentCircleBinding
import com.example.mvr.use.mvr.MainState
import com.example.mvr.use.mvr.MainViewModel

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