package com.mercadopago.android.px.internal.callbacks

internal interface DeepLinkServiceListener {

    fun onTokenization(state: TokenizationResponse.State)
}
