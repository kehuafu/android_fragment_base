package com.example.demo.use

import android.os.Bundle
import com.example.demo.app.Router
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
import com.example.demo.use.other.MainPage1Fragment
import com.example.demo.use.other.MainPage2Fragment
import com.example.demo.use.other.MainPage3Fragment
import com.kehuafu.base.core.container.base.BaseActivity
import com.kehuafu.base.core.fragment.RouterController
import com.kehuafu.base.core.fragment.constant.NavMode

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

    override fun navigation(name: Int, bundle: Bundle?, navMode: NavMode) {
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