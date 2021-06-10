package com.mercadopago.android.px.model

@Deprecated("Not used anymore")
data class Instructions(
    val amountInfo: AmountInfo?,
    val instructions: List<Instruction>?
)
