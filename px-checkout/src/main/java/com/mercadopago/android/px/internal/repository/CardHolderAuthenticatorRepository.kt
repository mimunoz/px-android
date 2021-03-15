package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.Site
import com.nds.nudetect.EMVAuthenticationRequestParameters

interface CardHolderAuthenticatorRepository {

    suspend fun authenticate(paymentData: PaymentData, card: Card, site: Site, currency: Currency, threeDSParams: EMVAuthenticationRequestParameters): Any
}