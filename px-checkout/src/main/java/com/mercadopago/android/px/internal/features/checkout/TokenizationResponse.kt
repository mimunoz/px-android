package com.mercadopago.android.px.internal.features.checkout

import com.google.gson.annotations.SerializedName

internal data class TokenizationResponse(val result: State) {

    enum class State {
        @SerializedName("success") SUCCESS,
        @SerializedName("pending") PENDING,
        @SerializedName("error") ERROR
    }
}
