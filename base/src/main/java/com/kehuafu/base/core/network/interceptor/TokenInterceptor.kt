package com.kehuafu.base.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Throws

/**
 *
 * on 2019-11-08
 *
 * desc:
 */
class TokenInterceptor private constructor() : Interceptor {

    companion object {
        private const val AUTHORIZATION = "Authorization"

        @JvmStatic
        fun create(): Interceptor = TokenInterceptor()
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val tmpRequest = chain
            .request()
            .newBuilder()
            .addHeader(AUTHORIZATION, "").build()
        return chain.proceed(tmpRequest)
    }
}