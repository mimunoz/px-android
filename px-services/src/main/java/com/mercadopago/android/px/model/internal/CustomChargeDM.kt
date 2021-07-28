package com.mercadopago.android.px.model.internal

import java.math.BigDecimal

data class CustomChargeDM(
    val charge: BigDecimal,
    val label: String?
)