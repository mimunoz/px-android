package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.callbacks.MPCall
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem

interface CheckoutRepository {
    fun checkout(): MPCall<CheckoutResponse>
    fun refreshWithNewCard(cardId: String): MPCall<CheckoutResponse>
    fun lazyConfigure(checkoutResponse: CheckoutResponse)
}

interface CheckoutRepositoryNew {
    fun checkout(): CheckoutResponse
    fun configure(checkoutResponse: CheckoutResponse)
    fun sortByPrioritizedCardId(oneTap: List<OneTapItem>, cardId: String)
}