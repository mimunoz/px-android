package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem

interface CheckoutRepository {
    suspend fun checkout(): ApiResponse<CheckoutResponse, ApiException>
    fun configure(checkoutResponse: CheckoutResponse)
    fun sortByPrioritizedCardId(oneTap: List<OneTapItem>, cardId: String)
}