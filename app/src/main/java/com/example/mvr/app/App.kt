package com.example.mvr.app

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources

/**
 *
 * on 2021-09-07
 *
 * desc:
 */
class App : Application() {

    companion object {
        @JvmStatic
        @Volatile
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        AppManager.registerAppService(appContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.fontScale != 1f) {//非默认值   强制让字体为默认
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    @Suppress("DEPRECATION", "DEPRECATED_IDENTITY_EQUALS")
    override fun getResources(): Resources? {
        val res = super.getResources()
        val fontScale = res.configuration.fontScale
        if (fontScale !== 1f) { //非默认值  强制让字体为默认
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }
}