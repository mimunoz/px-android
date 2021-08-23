package com.mercadopago.android.px.internal.callbacks

import com.google.gson.annotations.SerializedName

internal data class TokenizationResponse(val result: State, val message: String) {

    enum class State {
        @SerializedName("success") SUCCESS,
        @SerializedName("pending") PENDING,
        @SerializedName("error") ERROR
    }
}
