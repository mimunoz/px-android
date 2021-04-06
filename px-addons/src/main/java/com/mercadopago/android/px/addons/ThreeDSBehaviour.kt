package com.mercadopago.android.px.addons

import com.mercadopago.android.px.addons.model.ThreeDSDataOnlyParams

interface ThreeDSBehaviour {
    fun getAuthenticationParameters(): ThreeDSDataOnlyParams?
}