package com.example.demo.use.other

import android.os.Bundle
import com.example.demo.app.Router
import com.example.demo.databinding.FragmentMainPage3Binding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment
import com.kehuafu.base.core.fragment.constant.NavMode


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