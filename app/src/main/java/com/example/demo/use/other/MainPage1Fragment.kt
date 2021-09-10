package com.example.demo.use.other

import android.os.Bundle
import com.example.demo.app.Router
import com.example.demo.databinding.FragmentMainPage1Binding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseFragment

class MainPage1Fragment : BaseFragment<FragmentMainPage1Binding, MainViewModel, MainState>() {

    override fun onViewCreated(savedInstanceState: Bundle?) {
        viewBinding.btn.setOnClickListener {
            baseActivity.navigation(Router.PAGE2)
        }
    }
}