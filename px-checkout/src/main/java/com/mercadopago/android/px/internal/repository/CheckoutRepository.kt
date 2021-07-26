package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem

interface CheckoutRepository {
    //suspend fun checkout(): ApiResponse<CheckoutResponse, ApiException>
    suspend fun checkout(): Response<CheckoutResponse, MercadoPagoError>
    suspend fun checkoutWithNewCard(cardId: String): Response<CheckoutResponse, MercadoPagoError>
    fun configure(checkoutResponse: CheckoutResponse)
}