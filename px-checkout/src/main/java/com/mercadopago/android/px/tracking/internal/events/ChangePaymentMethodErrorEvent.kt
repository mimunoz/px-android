package com.mercadopago.android.px.tracking.internal.events


import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

  class ChangePaymentMethodErrorEvent (showedModel: Boolean) : TrackWrapper() {

    private val request = if (showedModel) "model" else "view"

    private fun getPath() = "$BASE_PATH/result/error/change_payment_method"
    private fun getData() = hashMapOf("from" to request)


    override fun getTrack() = TrackFactory.withEvent(getPath()).addData(getData()).build()

}








