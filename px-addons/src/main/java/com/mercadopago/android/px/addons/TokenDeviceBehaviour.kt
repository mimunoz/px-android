package com.mercadopago.android.px.addons

import com.mercadopago.android.px.addons.model.TokenState

interface TokenDeviceBehaviour {
    val isFeatureAvailable: Boolean
    val tokensStatus: List<TokenState>
    fun getTokenStatus(cardId: String): TokenState
}