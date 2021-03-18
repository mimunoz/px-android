package com.mercadopago.android.px.addons.internal

import com.mercadopago.android.px.addons.ThreeDSBehaviour
import com.mercadopago.android.px.addons.model.ThreeDSDataOnlyParams

class ThreeDSDefaultBehaviour : ThreeDSBehaviour {
    override fun getAuthenticationParameters(): ThreeDSDataOnlyParams? = null
}