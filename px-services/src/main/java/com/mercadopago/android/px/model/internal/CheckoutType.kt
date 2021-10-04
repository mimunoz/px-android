package com.mercadopago.android.px.model.internal

import com.google.gson.annotations.SerializedName

enum class CheckoutType {
    @SerializedName("regular") REGULAR,
    @SerializedName("scheduled") SCHEDULED
}