package com.example.mvr.use.other

import android.os.Bundle
import com.example.mvr.core.container.base.BaseFragment
import com.example.mvr.core.fragment.constant.NavMode
import com.example.mvr.core.fragment.constant.Router
import com.example.mvr.databinding.FragmentMainPage3Binding
import com.example.mvr.use.mvr.MainState
import com.example.mvr.use.mvr.MainViewModel


class MainPage3Fragment : BaseFragment<FragmentMainPage3Binding, MainViewModel, MainState>() {

    override fun onViewCreated(savedInstanceState: Bundle?) {
        viewBinding.btn.setOnClickListener {
            baseActivity.navigation(Router.PAGE2, navMode = NavMode.POP_BACK_STACK)
        }
        viewBinding.btn2.setOnClickListener {
            baseActivity.navigation(Router.PAGE1, navMode = NavMode.POP_BACK_STACK)
        }
    }
}