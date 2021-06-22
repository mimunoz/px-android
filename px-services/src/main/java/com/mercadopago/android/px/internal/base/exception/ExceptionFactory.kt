package com.mercadopago.android.px.internal.base.exception

import com.mercadopago.android.px.model.exceptions.ApiException

internal object ExceptionFactory {

    fun connectionError(): ApiException {
        return ApiException().apply{ message = "No connection" }
    }

    fun genericError(message: String? = null): ApiException {
        return ApiException().apply{ this.message = message.orEmpty() }
    }
}