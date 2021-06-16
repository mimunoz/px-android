package com.mercadopago.android.px.internal.domain

import android.os.Handler
import android.os.HandlerThread
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.repository.CheckoutRepositoryNew
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class CheckoutUseCase (
    private val checkoutRepository: CheckoutRepositoryNew,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<CheckoutUseCase.CheckoutParams, CheckoutResponse>(tracker) {
    /* default */
    private var retryHandler: Handler? = null
    /* default */
    private var refreshRetriesAvailable = MAX_REFRESH_RETRIES

    override suspend fun doExecute(param: CheckoutParams): Response<CheckoutResponse, MercadoPagoError> {
        val checkoutResponse = checkoutRepository.checkout()
        if (param.prioritizedCardId.isNotNull()){
            val retryAvailable: Boolean = --refreshRetriesAvailable > 0
            val oneTap = checkoutResponse.oneTapItems
            val (cardFound, retryNeeded) = findCard(oneTap, param.prioritizedCardId)
            if (cardFound && (!retryNeeded || !retryAvailable)) {
                handleCardFound(oneTap, param.prioritizedCardId, checkoutResponse)
            } else if (retryAvailable) {
                handleFindCardRetry(retryNeeded, param.prioritizedCardId)
            } else {
                return Response.Failure(MercadoPagoError("Exceeded max retries on new card", false))
            }
            return Response.Success(checkoutResponse)
        } else {
            checkoutRepository.configure(checkoutResponse)
            return Response.Success(checkoutResponse)
        }
    }

    private fun handleFindCardRetry(retryNeeded: Boolean, cardId: String) {
        val retryDelay = if (retryNeeded) LONG_RETRY_DELAY else DEFAULT_RETRY_DELAY
        if (retryHandler == null) {
            val thread = HandlerThread("MyInitRetryThread")
            thread.start()
            retryHandler = Handler(thread.looper)
        }
        retryHandler!!.postDelayed(Runnable { execute(CheckoutParams(cardId)) }, retryDelay.toLong())
    }

    private fun handleCardFound(oneTap: List<OneTapItem>, cardId: String, checkoutResponse: CheckoutResponse) {
        refreshRetriesAvailable = MAX_REFRESH_RETRIES
        checkoutRepository.sortByPrioritizedCardId(oneTap, cardId)
        checkoutRepository.configure(checkoutResponse)
    }

    private fun findCard(oneTap: List<OneTapItem>, cardId: String): Pair<Boolean, Boolean> {
        var cardFound = false
        var retryNeeded = false
        for (node in oneTap) {
            if (node.isCard && node.card.id == cardId) {
                cardFound = true
                retryNeeded = node.card.retry.isNeeded
                break
            }
        }
        return Pair(cardFound, retryNeeded)
    }

    data class CheckoutParams(
        val prioritizedCardId: String?
    )

    companion object {
        private const val MAX_REFRESH_RETRIES = 4
        private const val DEFAULT_RETRY_DELAY = 500
        private const val LONG_RETRY_DELAY = 5000
    }
}