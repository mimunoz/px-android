package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.model.internal.CardStatusDM

internal interface CardStatusRepository {
    fun getCardsStatus(): List<CardStatusDM>
}