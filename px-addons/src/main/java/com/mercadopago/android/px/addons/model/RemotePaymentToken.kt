package com.mercadopago.android.px.addons.model

import java.util.Date

data class RemotePaymentToken(val cryptogramData: ByteArray, val digitalPan: String, val par: String, val digitalPanExpirationDate: Date)
