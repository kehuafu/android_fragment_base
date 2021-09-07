package com.example.mvr.core.net


import com.google.gson.annotations.SerializedName

data class APIResponse<T>(
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("error_message")
    val errorMessage: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("result")
    val result: T? = null
) {

    companion object {
        private const val ERROR_RE_LOGIN = 10004  //token过期，请求重新登录
        private const val ERROR_UN_LOGIN = 10005  //未登录
        private const val ERROR_APP_VERSION = 10066 //当前版本无法访问该功能，请升级app
    }

    fun apiUnauthorized(): Boolean {
        return errorCode == ERROR_RE_LOGIN || errorCode == ERROR_UN_LOGIN
    }

    fun appVersionForbidden(): Boolean {
        return errorCode == ERROR_APP_VERSION
    }
}

