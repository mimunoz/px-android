package com.mercadopago.android.px.model.internal;

import com.mercadopago.android.px.addons.model.TokenState

internal data class CardStatusDM(val cardId: String, val tokenState: TokenStateDM, val hasEsc: Boolean) {
    enum class TokenStateDM {
        ENABLED,
        IN_PROGRESS,
        SUSPENDED,
        DELETED,
        NONE;

        companion object {
            fun from(tokenState: TokenState.State?): TokenStateDM {
                return when (tokenState) {
                    TokenState.State.ENABLED -> ENABLED
                    TokenState.State.IN_PROGRESS -> IN_PROGRESS
                    TokenState.State.DELETED -> DELETED
                    else -> NONE
                }
            }
        }
    }
}