package com.mercadopago.android.px.internal.domain

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class CheckoutUseCase (
    private val checkoutRepository: CheckoutRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<Unit, CheckoutResponse>(tracker) {

    override suspend fun doExecute(param: Unit): Response<CheckoutResponse, MercadoPagoError> {
        return when (val apiResponse = checkoutRepository.checkout()) {
            is ApiResponse.Failure ->
                Response.Failure(MercadoPagoError(apiResponse.exception, ApiUtil.RequestOrigin.POST_INIT))
            is ApiResponse.Success -> {
                checkoutRepository.configure(apiResponse.result)
                Response.Success(apiResponse.result)
            }
        }
    }
}