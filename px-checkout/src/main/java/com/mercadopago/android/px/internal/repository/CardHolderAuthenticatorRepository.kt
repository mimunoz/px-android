package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.PaymentData
import com.nds.nudetect.EMVAuthenticationRequestParameters

internal interface CardHolderAuthenticatorRepository {

    suspend fun authenticate(paymentData: PaymentData, threeDSParams: EMVAuthenticationRequestParameters): Any
}