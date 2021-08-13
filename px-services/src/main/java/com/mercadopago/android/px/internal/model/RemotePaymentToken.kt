package com.mercadopago.android.px.internal.model

import java.util.*

data class RemotePaymentToken(val cryptogramData: ByteArray, val digitalPan: String, val par: String, val digitalPanExpirationDate: Date)