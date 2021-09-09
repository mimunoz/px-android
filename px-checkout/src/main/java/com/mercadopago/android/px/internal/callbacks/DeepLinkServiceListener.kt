package com.mercadopago.android.px.internal.callbacks

internal interface DeepLinkServiceListener {

    fun onDefault()
    fun onTokenization(state: TokenizationResponse.State)
}
