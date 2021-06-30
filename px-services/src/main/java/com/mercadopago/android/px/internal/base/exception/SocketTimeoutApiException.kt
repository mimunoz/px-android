package com.mercadopago.android.px.internal.base.exception

import com.mercadopago.android.px.model.exceptions.ApiException

internal class SocketTimeoutApiException(message: String) : ApiException() {
    init {
        this.message = message
    }
}