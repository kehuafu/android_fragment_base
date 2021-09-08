package com.example.mvr.use

import android.os.Bundle
import com.example.mvr.core.container.base.BaseActivity
import com.example.mvr.core.fragment.RouterController
import com.example.mvr.core.fragment.constant.NavMode
import com.example.mvr.core.fragment.constant.Router
import com.example.mvr.databinding.ActivityMainBinding
import com.example.mvr.use.mvr.MainState
import com.example.mvr.use.mvr.MainViewModel
import com.example.mvr.use.other.MainPage1Fragment
import com.example.mvr.use.other.MainPage2Fragment
import com.example.mvr.use.other.MainPage3Fragment

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel, MainState>() {


    override fun onViewCreated(savedInstanceState: Bundle?) {
        //模拟传递一个数值
        val arg = Bundle()
        arg.putString("key", "Hello Router!")
        navigation(Router.MAIN, arg)
    }

    override fun frameLayoutId(): Int {
        return viewBinding.frameLayout.id
    }

    override fun navigation(name: Router, bundle: Bundle?, navMode: NavMode) {
        when (name) {
            Router.MAIN -> {
                RouterController.switcher(MainFragment::class.java, bundle, navMode, false)
            }
            Router.PAGE1 -> {
                RouterController.switcher(MainPage1Fragment::class.java, bundle, navMode)
            }
            Router.PAGE2 -> {
                RouterController.switcher(MainPage2Fragment::class.java, bundle, navMode)
            }
            Router.PAGE3 -> {
                RouterController.switcher(MainPage3Fragment::class.java, bundle, navMode)
            }
        }
    }
}