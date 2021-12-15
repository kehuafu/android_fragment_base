package com.example.demo.app

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.Utils
import com.kehuafu.base.core.container.widget.toast.setup

/**
 *
 * on 2021-09-06
 *
 * desc:
 */
object AppManager {

    private val TAG = AppManager::class.java.simpleName

    val apiService: PurpleAPI by lazy {
        NetManager().apiService
    }

    fun registerAppService(context: Context) {
        setup(context)
        Utils.init(context as Application)
    }
}