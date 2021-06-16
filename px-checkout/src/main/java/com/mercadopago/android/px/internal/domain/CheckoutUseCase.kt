package com.mercadopago.android.px.internal.domain

import android.os.Handler
import android.os.HandlerThread
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.repository.CheckoutRepositoryNew
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.delay

internal class CheckoutUseCase (
    private val checkoutRepository: CheckoutRepositoryNew,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<CheckoutUseCase.CheckoutParams, CheckoutResponse>(tracker) {
    /* default */
    private var refreshRetriesAvailable = MAX_REFRESH_RETRIES

    override suspend fun doExecute(param: CheckoutParams): Response<CheckoutResponse, MercadoPagoError> {
        var checkoutResponse = checkoutRepository.checkout()
        param.prioritizedCardId?.let { prioritizedCardId ->
            var findCardRes = findCard(checkoutResponse.oneTapItems, prioritizedCardId)
            while (cardNotFoundOrRetryNeeded(findCardRes) && hasRefreshRetriesAvailable()) {
                delay(findCardRes.retryDelay)
                --refreshRetriesAvailable
                checkoutResponse = checkoutRepository.checkout()
                findCardRes = findCard(checkoutResponse.oneTapItems, prioritizedCardId)
            }
            if (cardNotFoundOrRetryNeeded(findCardRes)) {
                return Response.Failure(
                    MercadoPagoError(ApiException(), ApiUtil.RequestOrigin.POST_INIT))
            }
            refreshRetriesAvailable = MAX_REFRESH_RETRIES
            checkoutRepository.sortByPrioritizedCardId(checkoutResponse.oneTapItems, prioritizedCardId)
        }
        checkoutRepository.configure(checkoutResponse)
        return Response.Success(checkoutResponse)
    }

    private fun hasRefreshRetriesAvailable(): Boolean {
        return refreshRetriesAvailable > 0
    }

    private fun cardNotFoundOrRetryNeeded(findCardRes : FindCardResult): Boolean {
        return (!findCardRes.cardFound || findCardRes.retryNeeded)
    }

    private fun findCard(oneTap: List<OneTapItem>, cardId: String): FindCardResult {
        var cardFound = false
        var retryNeeded = false
        for (node in oneTap) {
            if (node.isCard && node.card.id == cardId) {
                cardFound = true
                retryNeeded = node.card.retry.isNeeded
                break
            }
        }
        return FindCardResult(cardFound, retryNeeded)
    }

    data class CheckoutParams(
        val prioritizedCardId: String?
    )

    private data class FindCardResult(
        val cardFound: Boolean,
        val retryNeeded: Boolean,
        val retryDelay: Long = if (retryNeeded) LONG_RETRY_DELAY else DEFAULT_RETRY_DELAY
    )

    companion object {
        private const val MAX_REFRESH_RETRIES = 3
        private const val DEFAULT_RETRY_DELAY = 500L
        private const val LONG_RETRY_DELAY = 5000L
    }
}