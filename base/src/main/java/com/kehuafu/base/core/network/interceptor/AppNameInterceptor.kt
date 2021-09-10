package com.kehuafu.base.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

/**
 *
 * on 2019-11-08
 *
 * desc:app名字拦拦截器
 */
class AppNameInterceptor private constructor() : Interceptor {

    companion object {

        @JvmStatic
        fun create(): Interceptor = AppNameInterceptor()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val tmpRequest = chain
            .request()
            .newBuilder()
            .addHeader("App-Name", "appName").build()
        return chain.proceed(tmpRequest)
    }
}