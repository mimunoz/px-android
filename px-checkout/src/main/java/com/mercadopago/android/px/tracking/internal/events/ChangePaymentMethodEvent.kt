package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

class ChangePaymentMethodEvent(viewTrack: TrackWrapper? = null, showedModel: Boolean, isFromRemedies: Boolean) : TrackWrapper() {

    private val eventPath = if(isFromRemedies) MODAL_PATH
        else (viewTrack?.getTrack()?.path ?: "$BASE_PATH/review/traditional") + CHANGE_PATH

    private val event = if(showedModel) "model" else "view"
    private val body = hashMapOf("from" to event)
    private val shouldAddData = isFromRemedies

    override fun getTrack() = if(shouldAddData) TrackFactory.withEvent(eventPath).addData(body).build()
        else TrackFactory.withEvent(eventPath).build()

    companion object {
        private const val CHANGE_PATH = "/change_payment_method"
        private const val MODAL_PATH = "${BASE_PATH}/result/error/change_payment_method"
    }
}