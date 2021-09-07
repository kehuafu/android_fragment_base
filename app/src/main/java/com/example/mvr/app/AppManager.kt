package com.example.mvr.app

import android.content.Context
import com.blankj.utilcode.util.Utils
import com.example.mvr.core.net.NetManager
import com.example.mvr.core.net.PurpleAPI
import com.example.mvr.core.container.widget.toast.setup

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
        Utils.init(context)
    }
}