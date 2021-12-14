package com.example.demo.use

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.example.demo.app.Router
import com.example.demo.databinding.ActivityMainBinding
import com.example.demo.use.mvvm.MainState
import com.example.demo.use.mvvm.MainViewModel
import com.example.demo.use.other.MainPage1Fragment
import com.example.demo.use.other.MainPage2Fragment
import com.example.demo.use.other.MainPage3Fragment
import com.example.demo.utils.StatusBarUtil
import com.kehuafu.base.core.container.base.BaseActivity
import com.kehuafu.base.core.fragment.RouterController
import com.kehuafu.base.core.fragment.constant.NavMode

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel, MainState>() {


    override fun onViewCreated(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED//禁止横屏
//        StatusBarUtil.transparencyBar(this) //设置状态栏全透明
        StatusBarUtil.StatusBarLightMode(this) //设置白底黑字
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
        }
    }
}