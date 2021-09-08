package com.example.mvr.use.other

import android.os.Bundle
import com.example.mvr.core.container.base.BaseFragment
import com.example.mvr.core.fragment.constant.Router
import com.example.mvr.databinding.FragmentMainPage1Binding
import com.example.mvr.use.mvr.MainState
import com.example.mvr.use.mvr.MainViewModel

class MainPage1Fragment : BaseFragment<FragmentMainPage1Binding, MainViewModel, MainState>() {

    override fun onViewCreated(savedInstanceState: Bundle?) {
        viewBinding.btn.setOnClickListener {
            baseActivity.navigation(Router.PAGE2)
        }
    }
}