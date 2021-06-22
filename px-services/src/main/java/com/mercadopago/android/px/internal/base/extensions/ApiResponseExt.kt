package com.mercadopago.android.px.internal.base.extensions

import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.core.extensions.orIfEmpty
import com.mercadopago.android.px.model.exceptions.ApiException

internal typealias ApiResponseCallback<R> = ApiResponse<R, ApiException>

fun <V, R> ApiResponseCallback<V>.map(transform: (V) -> R): ApiResponseCallback<R> {
    return when (this) {
        is ApiResponse.Success -> {
            try {
                success(transform(result))
            } catch (e: Exception) {
                failure(ApiException().apply {
                    e.localizedMessage.orIfEmpty("transform operation is not supported")
                })
            }
        }
        is ApiResponse.Failure -> failure(exception)
    }
}

fun <T> ApiResponseCallback<T>.fold(
    complete: () -> Unit,
    success: (value: T) -> Unit,
    error: (error: ApiException) -> Unit
) {
    when (this) {
        is ApiResponse.Success -> { success(result) ; complete() }
        is ApiResponse.Failure -> error(exception)
    }
}