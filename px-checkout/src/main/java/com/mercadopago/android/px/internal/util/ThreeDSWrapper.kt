package com.mercadopago.android.px.internal.util

import android.util.Log
import com.mercadopago.android.px.internal.di.Session
import com.nds.nudetect.EMVAuthenticationRequestParameters
import com.nds.threeds.core.EMVThreeDS2Service
import com.nds.threeds.core.ThreeDSInitializationCallback
import com.nds.threeds.core.ThreeDSSDK

object ThreeDSWrapper {

    lateinit var threeDSSDK: ThreeDSSDK
    lateinit var threeDS2Service: EMVThreeDS2Service

    fun initialize() {
        threeDSSDK = ThreeDSSDK.Builder().build(Session.getInstance().applicationContext)
        threeDSSDK.initialize(object : ThreeDSInitializationCallback {
            override fun error(e: Exception?) {
                // do nothing
                Log.v("CRIS 4", e.toString())
            }

            override fun success() {
                threeDS2Service = threeDSSDK.threeDS2Service()
            }
        })
    }

    fun getAuthenticationParameters(): EMVAuthenticationRequestParameters {
        val transaction = threeDS2Service.createTransaction("A000000004", "2.1.0")
        val authenticationRequestParameters = transaction.authenticationRequestParameters
        Log.v("CRIS 3", authenticationRequestParameters.deviceData)
        return authenticationRequestParameters
    }
}