package com.mercadopago.android.px.addons

import com.mercadopago.android.px.addons.model.RemotePaymentToken
import com.mercadopago.android.px.addons.model.TokenState
import java.math.BigDecimal

interface TokenDeviceBehaviour {
    val isFeatureAvailable: Boolean
    val tokensStatus: List<TokenState>
    fun getTokenStatus(cardId: String): TokenState
    suspend fun getRemotePaymentToken(cardId: String, amount: BigDecimal): RemotePaymentToken

}