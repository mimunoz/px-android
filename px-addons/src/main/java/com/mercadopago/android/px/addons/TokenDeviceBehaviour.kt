package com.mercadopago.android.px.addons

import com.mercadopago.android.px.addons.model.RemotePaymentToken
import java.math.BigDecimal

interface TokenDeviceBehaviour {
    val isFeatureAvailable: Boolean
    suspend fun getRemotePaymentToken(cardId: String, amount: BigDecimal): RemotePaymentToken
}