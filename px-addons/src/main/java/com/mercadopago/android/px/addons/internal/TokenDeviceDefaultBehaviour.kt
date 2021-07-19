package com.mercadopago.android.px.addons.internal

import com.mercadopago.android.px.addons.TokenDeviceBehaviour
import com.mercadopago.android.px.addons.model.RemotePaymentToken
import java.math.BigDecimal

internal class TokenDeviceDefaultBehaviour : TokenDeviceBehaviour {
    override val isFeatureAvailable: Boolean = false
    override suspend fun getRemotePaymentToken(cardId: String, amount: BigDecimal): RemotePaymentToken {
        throw NotImplementedError("Remote payment is not implemented")
    }
}