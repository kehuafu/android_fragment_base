package com.example.demo.app

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.Utils
import com.example.demo.BuildConfig
import com.example.demo.common.receiver.LocalEventLifecycleViewModel
import com.example.demo.im.*
import com.kehuafu.base.core.container.widget.toast.setup
import com.tencent.wstt.gt.controller.GTRController

/**
 *
 * on 2021-09-06
 *
 * desc:
 */
object AppManager {

    private val TAG = AppManager::class.java.simpleName

    const val currentUserID = "android"
    
    val apiService: PurpleAPI by lazy {
        NetManager().apiService
    }

    val iCloudImManager: ICloudImManager by lazy {
        CloudImManager.create()
    }

    val iCloudMessageManager: ICloudMessageManager by lazy {
        CloudMessageManager.create()
    }

    val iCloudConversationManager: ICloudConversationManager by lazy {
        CloudConversationManager.create()
    }

    val iCloudGroupManager: ICloudGroupManager by lazy {
        CloudGroupManager.create()
    }

    val iCloudUserManager: ICloudUserManager by lazy {
        CloudUserManager.create()
    }

    val localEventLifecycleViewModel by lazy {
        LocalEventLifecycleViewModel(App.appContext as Application)
    }

    fun registerAppService(context: Context) {
        setup(context)
        Utils.init(context as Application)
        iCloudImManager.initSDK(context, BuildConfig.CLOUD_IM_APP_KEY)
//        GTRController.init(context)
    }
}