package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper
import com.mercadopago.android.px.tracking.internal.model.RemedyTrackData

internal class RemedyEvent(private val data: RemedyTrackData, showedModel: Boolean, isConsumerCredits: Boolean): TrackWrapper() {

    private val route = if (showedModel && isConsumerCredits) MODAL_PATH else PATH

    override fun getTrack() = TrackFactory.withEvent(route)
        .addData(data.toMap()).build()

    companion object {
        private const val PATH = "$BASE_PATH/result/error/remedy"
        private const val MODAL_PATH = "$BASE_PATH/result/error/remedy/modal"
    }
}