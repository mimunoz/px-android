package com.mercadopago.android.px.model

import com.google.gson.annotations.SerializedName

enum class AccountMoneyDisplayInfoType {
    @SerializedName("default") DEFAULT,
    @SerializedName("hybrid") HYBRID;
}

enum class CardDisplayInfoType  {
    @SerializedName("default") DEFAULT,
    @SerializedName("hybrid") HYBRID
}