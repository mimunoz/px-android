package com.mercadopago.android.px.addons.model

data class TokenState(val cardId: String, val state: State) {
    enum class State {
        ENABLED,
        IN_PROGRESS,
        SUSPENDED,
        DELETED,
        NONE
    }
}