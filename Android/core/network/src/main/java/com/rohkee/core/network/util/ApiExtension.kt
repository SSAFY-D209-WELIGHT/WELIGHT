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
