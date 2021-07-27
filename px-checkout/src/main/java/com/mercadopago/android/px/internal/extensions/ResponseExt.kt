package com.mercadopago.android.px.internal.extensions

import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

inline fun <T> Response<T, MercadoPagoError>.ifSuccess(
    block: (value: T) -> Unit
): Response<T, MercadoPagoError> {
    return when (this) {
        is Response.Success -> {
            block(result)
            success(result)
        }
        is Response.Failure -> failure(exception)
    }
}
