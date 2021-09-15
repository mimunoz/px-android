package com.mercadopago.android.px.internal.features.payment_result.remedies

internal enum class CardSize(private val size: String) {
    LARGE("large"),
    MEDIUM("medium"),
    SMALL("small"),
    XSMALL("xsmall"),
    MINI("mini");

    fun getType() = size
}