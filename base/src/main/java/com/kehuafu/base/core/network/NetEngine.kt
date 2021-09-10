package com.kehuafu.base.core.network

import com.google.gson.GsonBuilder
import com.kehuafu.base.core.network.interceptor.NormalInterceptor
import okhttp3.Dns
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by light
 *
 * on 2021/09/06
 *
 * desc:网络引擎
 *
 */

class NetEngine private constructor(
    baseUrl: String,
    debug: Boolean = false,
    dns: Dns? = null,
    interceptors: Array<out Interceptor>?
) {

    companion object {
        private const val TIMEOUT = 8L
    }

    private val mRetrofit by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .protocols(listOf(Protocol.HTTP_1_1, Protocol.HTTP_2))
            //.sslSocketFactory(createSSLServerSocketFactory(), createTrustManager())
            .dns(dns ?: Dns.SYSTEM)
            //.hostnameVerifier(HostnameVerifier { hostname, session -> hostname == session?.peerHost })
            .addInterceptor(NormalInterceptor.create())
            .addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level =
                    if (debug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })

        interceptors?.forEach {
            okHttpClientBuilder.addInterceptor(it)
        }

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .callFactory(okHttpClientBuilder.build())
            .build()
    }

    /**
     * 可以使用该函数获取相同 base_url 下的 retrofit e.g. 按模块去实例化不同网络请求 Api
     */
    fun getInstalledRetrofit(): Retrofit {
        return mRetrofit
    }

    /**
     * 可以使用该 函数 创建一个网络请求 Api
     */
    fun <NetworkApi> create(clx: Class<out NetworkApi>): NetworkApi {
        return mRetrofit.create(clx)
    }

    class NetEngineBuilder {
        private lateinit var mBaseUrl: String
        private var mDebug = false
        private var mInterceptors: Array<out Interceptor>? = null
        private var mDns: Dns? = null
        fun debug(debug: Boolean = false): NetEngineBuilder {
            this.mDebug = debug
            return this
        }

        fun baseUrl(baseUrl: String): NetEngineBuilder {
            this.mBaseUrl = baseUrl
            return this
        }

        fun dns(dns: Dns = Dns.SYSTEM): NetEngineBuilder {
            this.mDns = dns
            return this
        }

        /**
         * you can add many interceptor   e.g. tokenInterceptor
         */
        fun addInterceptors(vararg interceptors: Interceptor): NetEngineBuilder {
            this.mInterceptors = interceptors
            return this
        }

        fun build(): NetEngine {
            return NetEngine(this.mBaseUrl, this.mDebug, this.mDns, this.mInterceptors)
        }

    }

}
