package com.mercadopago.android.px.internal.model

import com.mercadopago.android.px.model.Device

data class CardTokenBody constructor(
    val cardId: String,
    val device: Device,
    val requireEsc: Boolean,
    val securityCode: String,
    val esc: String,
    val tokenization: RemotePaymentToken? = null
) {
    constructor(cardId: String, device: Device, securityCode: String): this(cardId, device, false, securityCode, "", null)
    constructor(cardId: String, device: Device, tokenization: RemotePaymentToken?): this(cardId, device, false, "", "", tokenization)

}