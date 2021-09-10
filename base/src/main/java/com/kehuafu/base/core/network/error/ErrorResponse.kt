package com.kehuafu.base.core.network.error

import com.google.gson.annotations.SerializedName

/**
 * 服务器错误 Response
 */
data class ErrorResponse(
    @SerializedName("error_code") val errorCode: Int,
    @SerializedName("error_msg") val errorMsg: String
) {
    companion object {
        const val ERROR_209 = 209
        const val ERROR_UNKNOWN = -1
        const val ERROR_UNAUTHORIZED = 401
        const val ERROR_FORBIDDEN = 403
        const val ERROR_NOT_FOUND = 404
        const val ERROR_SERVICE_UNAVAILABLE = 503

        const val ERROR_APP_VERSION = 498
        const val ERROR_BIZ_499 = 499

        @JvmStatic
        fun error499(error: String): ErrorResponse = createError(ERROR_BIZ_499, error)

        @JvmStatic
        fun errorUnknown(error: String? = null): ErrorResponse = createError(ERROR_UNKNOWN, error ?: "unknown error")

        @JvmStatic
        fun createError(code: Int, error: String): ErrorResponse = ErrorResponse(code, error)
    }

    fun unauthorized(): Boolean {
        return errorCode == ERROR_UNAUTHORIZED
    }

    fun appVersionForbidden(): Boolean {
        return errorCode == ERROR_APP_VERSION
    }
}
