package com.kehuafu.base.core.ktx

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kehuafu.base.core.network.error.ErrorResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 *  Created by sai
 *  on 2020/8/16
 *
 *  desc:
 */
class ViewModelKtx {
}

@Suppress("BlockingMethodInNonBlockingContext")
fun ViewModel.asyncCall(failedBlock: ((ErrorResponse) -> Unit)? = null, execute: suspend () -> Unit) {
    viewModelScope.launch {
        try {
            execute.invoke()
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is HttpException -> {
                    val code = e.code()
                    when {
                        code == ErrorResponse.ERROR_BIZ_499 -> {
                            val errorBody = e.response()?.errorBody()?.string() ?: ""
                            if (TextUtils.isEmpty(errorBody)) {
                                failedBlock?.invoke(ErrorResponse.error499(error = e.message()))
                            } else {
                                failedBlock?.invoke(errorBody.toObj())
                            }
                        }
                        code >= 500 -> {
                            failedBlock?.invoke(ErrorResponse.createError(e.code(), "服务器开小差了"))
                        }
                        else -> {
                            failedBlock?.invoke(ErrorResponse.createError(e.code(), e.message()))
                        }
                    }
                }
                is CancellationException -> {
                    Log.e("CancellationException", e.message ?: "")
                    return@launch
                }
                is UnknownHostException -> {
                    val errorResponse = ErrorResponse.errorUnknown("似乎已断开互联网的连接")
                    failedBlock?.invoke(errorResponse)
                }
                is ConnectException -> {
                    val errorResponse = ErrorResponse.errorUnknown("似乎已断开互联网的连接")
                    failedBlock?.invoke(errorResponse)
                }
                is SocketTimeoutException -> {
                    val errorResponse = ErrorResponse.errorUnknown("似乎已断开互联网的连接")
                    failedBlock?.invoke(errorResponse)
                }
                else -> {
                    val errorResponse = ErrorResponse.errorUnknown(e.message)
                    failedBlock?.invoke(errorResponse)
                }
            }
        } finally {
            Log.e("TAG", "finally")
        }
    }
}