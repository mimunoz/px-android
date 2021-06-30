package com.mercadopago.android.px.internal.base.exception

import com.mercadopago.android.px.internal.core.extensions.orIfEmpty
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import retrofit2.HttpException

internal object ExceptionFactory {

    fun connectionError() = ApiException().also { it.message = "No connection" }

    fun genericError(message: String? = null) = ApiException().also { it.message = message.orEmpty() }

    fun httpError(throwable: HttpException) = JsonUtil.fromJson(
        throwable.response()?.errorBody().toString(),
        ApiException::class.java
    )!!

    fun socketTimeoutError(message: String? = null) = SocketTimeoutApiException(message.orIfEmpty("Socket timeout"))
}
