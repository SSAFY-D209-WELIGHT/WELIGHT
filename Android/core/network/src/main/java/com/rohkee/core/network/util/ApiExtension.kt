package com.rohkee.core.network.util

import com.rohkee.core.network.ApiResponse
import com.rohkee.core.network.ResponseBody

fun <T : ResponseBody<R>, R : Any> ApiResponse<T>.simplify(): ApiResponse<R> =
    when (this) {
        is ApiResponse.Success -> {
            ApiResponse.Success(this.body?.data)
        }

        is ApiResponse.Error -> {
            ApiResponse.Error(
                errorCode = this.errorCode,
                errorMessage = this.errorMessage,
            )
        }
    }

suspend fun <T> ApiResponse<T>.handle(
    onSuccess: suspend (T?) -> Unit,
    onError: suspend (errorCode: Int?, message: String?) -> Unit,
) {
    when (this) {
        is ApiResponse.Success -> {
            onSuccess(this.body)
        }

        is ApiResponse.Error -> {
            onError(this.errorCode, this.errorMessage)
        }
    }
}

suspend fun <T, R> ApiResponse<T>.process(
    onSuccess: suspend (T?) -> R,
    onError: suspend (errorCode: Int?, message: String?) -> R,
): R =
    when (this) {
        is ApiResponse.Success -> {
            onSuccess(this.body)
        }

        is ApiResponse.Error -> {
            onError(this.errorCode, this.errorMessage)
        }
    }
