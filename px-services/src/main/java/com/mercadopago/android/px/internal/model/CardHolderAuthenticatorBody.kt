package com.mercadopago.android.px.internal.model

import com.mercadopago.android.px.model.Currency


data class CardHolderAuthenticatorBody(
    val purchaseAmount: String,
    val card: Card,
    val currency: Currency,
    val siteId: String,
    val sdkAppId: String,
    val sdkEncData: String,
    val sdkEphemPubKey: SdkEphemPubKey,
    val sdkReferenceNumber: String,
    val sdkTransId: String,
    val sdkMaxTimeout: String = "06") {

    data class Card(
        val cardholderName: String?,
        val paymentMethod: String?
    )

    data class SdkEphemPubKey(
        val kty: String,
        val crv: String,
        val x: String,
        val y: String)
}