package com.mercadopago.android.px.addons.model

data class ThreeDSDataOnlyParams(
    val sdkAppId: String,
    val deviceData: String,
    val sdkEphemeralPublicKey: String,
    val sdkReferenceNumber: String,
    val sdkTransactionId: String
)