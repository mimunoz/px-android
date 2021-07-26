package com.mercadopago.android.px.internal.domain

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.next
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.internal.repository.OneTapItemRepository
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class CheckoutWithNewCardUseCase(
    private val checkoutRepository: CheckoutRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider(),
    val oneTapItemRepository: OneTapItemRepository
) : UseCase<String, CheckoutResponse>(tracker) {

    override suspend fun doExecute(param: String): Response<CheckoutResponse, MercadoPagoError> {
        return checkoutRepository.checkoutWithNewCard(param).next {
            oneTapItemRepository.sortByPrioritizedCardId(it.oneTapItems, param)
            checkoutRepository.configure(it)
            Response.Success(it)
        }
    }
}

