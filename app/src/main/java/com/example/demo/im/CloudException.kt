package com.example.demo.im

import com.kehuafu.base.core.network.error.ErrorResponse


class CloudException(private val errorResponse: ErrorResponse) :
    IllegalArgumentException(errorResponse.errorMsg) {

    fun errorCode(): Int = errorResponse.errorCode
    fun errorMsg(): String = errorResponse.errorMsg
    fun errorResponse(): ErrorResponse = errorResponse
}