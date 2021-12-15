package com.example.demo.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.blankj.utilcode.util.BarUtils
import com.example.demo.R
import com.example.demo.app.Router
import com.example.demo.chat.ChatFragment
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.main.mvvm.MainState
import com.example.demo.main.mvvm.MainViewModel
import com.kehuafu.base.core.container.base.BaseActivity
import com.kehuafu.base.core.fragment.RouterController
import com.kehuafu.base.core.fragment.constant.NavMode

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel, MainState>() {


    override fun onViewCreated(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED//禁止横屏
        BarUtils.setStatusBarLightMode(this, true)
        BarUtils.setNavBarColor(this, resources.getColor(R.color.tab_nav_background))
        BarUtils.setNavBarLightMode(this, true)
        //模拟传递一个数值
        val arg = Bundle()
        arg.putString("key", "Hello Router!")
        navigation(Router.MAIN_FRAGMENT, arg)
    }

    override fun frameLayoutId(): Int {
        return viewBinding.frameLayout.id
    }

    override fun navigation(name: Int, bundle: Bundle?, navMode: NavMode) {
        when (name) {
            Router.MAIN_FRAGMENT -> {
                RouterController.switcher(MainFragment::class.java, bundle, navMode, false)
            }
            Router.CHAT_FRAGMENT -> {
                RouterController.switcher(ChatFragment::class.java, bundle, navMode)
            }
        }
    }
}