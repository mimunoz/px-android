package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem

interface CheckoutRepository {
    suspend fun checkout(): CheckoutResponse
    fun configure(checkoutResponse: CheckoutResponse)
    fun sortByPrioritizedCardId(oneTap: List<OneTapItem>, cardId: String)
}