package com.mercadopago.android.px.model.internal

data class DiscountParamsConfigurationDM(
    val labels: Set<String>,
    val productId: String?,
    val additionalParams: Map<String, String>
)