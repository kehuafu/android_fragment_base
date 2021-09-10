package com.kehuafu.base.core.network.interceptor

import com.blankj.utilcode.util.AppUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.*
import kotlin.jvm.Throws

/**
 * Created by jzz
 * on 2017/9/26
 *
 *
 * desc:  一般http/https 请求常规拦截器  包括 为请求添加UA/Host/Accept-Language/content-Type...
 */

class NormalInterceptor private constructor() : Interceptor {

    companion object {
        @JvmStatic
        fun create(): NormalInterceptor {
            return NormalInterceptor()
        }
    }

    private val userAgent: String
        get() {
            val userAgent: String = System.getProperty("http.agent") ?: ""

            val sb = StringBuilder()
            var i = 0
            val length = userAgent.length
            while (i < length) {
                val c = userAgent[i]
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", c.toInt()))
                } else {
                    sb.append(c)
                }
                i++
            }
            return sb.toString()
        }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain
            .request()
            .newBuilder()
            .addHeader("Accept-Language", Locale.getDefault().language)
            .addHeader("Content-Type", "application/json")
            //.addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("User-Agent", userAgent)
            .addHeader("App-Version", getAppVersion())
            .addHeader("App-Platform", "Android")
            .addHeader("Host", chain.request().url.host)
        return chain.proceed(builder.build())
    }

    private fun getAppVersion(): String {
        val appVersionName = AppUtils.getAppVersionName()
        val appVersionNameSplit = appVersionName.split("_")
        return if (appVersionNameSplit.isNullOrEmpty()) appVersionName else appVersionNameSplit[0]  //app_version=1.3.0_dev
    }
}
