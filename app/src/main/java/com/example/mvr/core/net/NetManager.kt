package com.example.mvr.core.net

import com.example.mvr.core.net.interceptor.AppNameInterceptor
import com.example.mvr.core.net.interceptor.ResponseInterceptor
import com.example.mvr.core.net.interceptor.TokenInterceptor

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
        NetEngine
            .NetEngineBuilder()
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