package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.callbacks.MPCall
import com.mercadopago.android.px.model.internal.InitResponse

interface InitRepository {
    fun init(): MPCall<InitResponse>
    fun refreshWithNewCard(cardId: String): MPCall<InitResponse>
    fun lazyConfigure(initResponse: InitResponse)
}