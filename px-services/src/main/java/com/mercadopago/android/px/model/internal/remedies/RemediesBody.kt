package com.mercadopago.android.px.model.internal.remedies

data class RemediesBody(
    val payerPaymentMethodRejected: RemedyPaymentMethod,
    val alternativePayerPaymentMethods: List<RemedyPaymentMethod>?,
    val customStringConfiguration: CustomStringConfiguration
)