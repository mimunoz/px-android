package com.mercadopago.android.px.addons

import com.mercadopago.android.px.addons.model.ThreeDSDataOnlyParams

class FakeThreeDSBehaviourImpl : ThreeDSBehaviour {
    override fun getAuthenticationParameters(): ThreeDSDataOnlyParams? {
        with(ThreeDSWrapper.getAuthenticationParameters()) {
            return ThreeDSDataOnlyParams(
                sdkAppID,
                deviceData,
                sdkEphemeralPublicKey,
                sdkReferenceNumber,
                sdkTransactionID
            )
        }
    }
}