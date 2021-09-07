package com.example.mvr.core.net.interceptor

import com.example.mvr.core.ktx.toJsonTxt
import com.example.mvr.core.ktx.toObj
import com.example.mvr.core.net.APIResponse
import com.example.mvr.core.net.error.ErrorResponse
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import kotlin.jvm.Throws

/**
 *
 * created by
 *
 * on 2019-08-22
 *
 * desc: 当请求body为null时,统一转义一下,防止json 解析出错
 *
 **/
class ResponseInterceptor private constructor() : Interceptor {

    companion object {
        private const val TAG = "ResponseInterceptor"

        @JvmStatic
        fun create(): Interceptor {
            return ResponseInterceptor()
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val bodyString = response.body?.string() ?: ""
        //如果出现 error  dispatcher close   因为response.body?.string()  方法会导致流关闭，从而下一个拦截器去使用 body 时，出现异常。因为流已经关闭。
        //所以要用方案 保证流不被关闭，其实就是重新传递给下一个拦截器 response.body()
        //类似的 request 也是一样。如果拦截了 request.body() 使用了。。。传递给下一个拦截器时，还是一样的要重新传递一个request.body()
        return if (response.isSuccessful) { //200-299
            if (bodyString.isBlank()) { //put patch delete 等操作，没有返回值，或者为 null，表示操作成功
                val errorResponse = APIResponse<Any>(response.code, response.message, true)
                val toResponseBody = errorResponse.toJsonTxt().toResponseBody()
                response.newBuilder().body(toResponseBody).build()
            } else {//表示带有数据返回，数据可能为 null
                try {
                    val apiResponse = bodyString.toObj<APIResponse<*>>()
                    if (apiResponse.success) {
                        val result = apiResponse.result ?: Unit
                        val resultBody = result.toJsonTxt().toResponseBody()
                        response.newBuilder().body(resultBody).build()
                    } else {
                        val errorResponse = ErrorResponse.createError(
                            apiResponse.errorCode,
                            apiResponse.errorMessage
                        )
                        when {
                            apiResponse.apiUnauthorized() -> {//未认证,token失效
                                response.newBuilder().code(ErrorResponse.ERROR_UNAUTHORIZED)
                                    .message(apiResponse.errorMessage)
                                    .body(errorResponse.toJsonTxt().toResponseBody()).build()
                            }
                            apiResponse.appVersionForbidden() -> { //客户端版本不一致，需要升级版本
                                response.newBuilder().code(ErrorResponse.ERROR_APP_VERSION)
                                    .message(apiResponse.errorMessage)
                                    .body(errorResponse.toJsonTxt().toResponseBody()).build()
                            }
                            else -> {//其它业务错误码
                                response.newBuilder().code(ErrorResponse.ERROR_BIZ_499)
                                    .message("Business Error")
                                    .body(errorResponse.toJsonTxt().toResponseBody())
                                    .build()
                            }
                        }
                    }
                } catch (e: Exception) {
                    val errorResponse = ErrorResponse.errorUnknown(bodyString)
                    response.newBuilder().code(ErrorResponse.ERROR_BIZ_499)
                        .message("Business Error").body(errorResponse.toJsonTxt().toResponseBody())
                        .build()
                }
            }
        } else { //请求出现了异常 code e.g. 400+
            response.newBuilder().body(bodyString.toResponseBody()).build()
        }
    }
}