package com.mercadopago.android.px.addons.internal

import com.mercadopago.android.px.addons.TokenDeviceBehaviour
import com.mercadopago.android.px.addons.model.TokenState

internal class TokenDeviceDefaultBehaviour : TokenDeviceBehaviour {
    override val isFeatureAvailable: Boolean = false
    override val tokensStatus: List<TokenState> = listOf()
    override fun getTokenStatus(cardId: String) = TokenState(cardId, TokenState.State.NONE)
}