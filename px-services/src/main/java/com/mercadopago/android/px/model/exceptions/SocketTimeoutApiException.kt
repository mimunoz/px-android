package com.mercadopago.android.px.model.exceptions


class SocketTimeoutApiException(message: String) : ApiException() {
    init {
        this.message = message
    }
}