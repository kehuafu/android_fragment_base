package com.example.demo.app

import com.kehuafu.base.core.network.NetEngine
import com.kehuafu.base.core.network.interceptor.AppNameInterceptor
import com.kehuafu.base.core.network.interceptor.ResponseInterceptor
import com.kehuafu.base.core.network.interceptor.TokenInterceptor

/**
 * Created by light
 *
 * on 2021/09/06
 *
 * desc:
 *
 */
class NetManager {
    val apiService: PurpleAPI by lazy {
        NetEngine.NetEngineBuilder()
            .baseUrl("https://www.baidu.com")
            .addInterceptors(
                AppNameInterceptor.create(),
                ResponseInterceptor.create(),
                TokenInterceptor.create()
            )
            .debug(true)
            .build()
            .create(PurpleAPI::class.java)
    }
}