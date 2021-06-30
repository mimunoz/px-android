package com.mercadopago.android.px.internal.domain

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.delay


internal class CheckoutWithNewCardUseCase(
    private val checkoutRepository: CheckoutRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<String, CheckoutResponse>(tracker) {
    /* default */
    private var refreshRetriesAvailable = MAX_REFRESH_RETRIES

    override suspend fun doExecute(param: String): Response<CheckoutResponse, MercadoPagoError> {
        return when (val apiResponse = checkoutRepository.checkout()) {
            is ApiResponse.Failure ->
                Response.Failure(MercadoPagoError(apiResponse.exception, ApiUtil.RequestOrigin.POST_INIT))
            is ApiResponse.Success -> {
                var checkoutResponse = apiResponse.result
                val findCardRes = findCardWithRetries(checkoutResponse, param)
                if (!findCardRes.cardFound) {
                    return Response.Failure(
                        MercadoPagoError(
                            ApiException().also { it.message = "Card not found" },
                            ApiUtil.RequestOrigin.POST_INIT
                        )
                    )
                }
                // Update checkoutResponse with the last retry made (if we retried with success)
                checkoutResponse = findCardRes.retriedCheckoutResponse ?: checkoutResponse
                checkoutRepository.sortByPrioritizedCardId(checkoutResponse.oneTapItems, param)
                checkoutRepository.configure(checkoutResponse)
                Response.Success(checkoutResponse)
            }
        }
    }

    private suspend fun findCardWithRetries(
        checkoutResponse: CheckoutResponse,
        prioritizedCardId: String
    ): FindCardResult {
        var findCardRes = findCard(checkoutResponse.oneTapItems, prioritizedCardId)
        var retriedCheckoutResponse: CheckoutResponse? = null
        loop@ while (cardNotFoundOrRetryNeeded(findCardRes) && hasRefreshRetriesAvailable()) {
            delay(findCardRes.retryDelay)
            --refreshRetriesAvailable
            when (val retryResponse = checkoutRepository.checkout()) {
                is ApiResponse.Failure -> continue@loop
                is ApiResponse.Success -> {
                    retriedCheckoutResponse = retryResponse.result
                    findCardRes = findCard(retriedCheckoutResponse.oneTapItems, prioritizedCardId)
                }
            }
        }
        refreshRetriesAvailable = MAX_REFRESH_RETRIES
        findCardRes.retriedCheckoutResponse = retriedCheckoutResponse
        return findCardRes
    }

    private fun hasRefreshRetriesAvailable(): Boolean {
        return refreshRetriesAvailable > 0
    }

    private fun cardNotFoundOrRetryNeeded(findCardRes: FindCardResult): Boolean {
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

    private data class FindCardResult(
        val cardFound: Boolean,
        val retryNeeded: Boolean,
        val retryDelay: Long = if (retryNeeded) LONG_RETRY_DELAY else DEFAULT_RETRY_DELAY,
        var retriedCheckoutResponse: CheckoutResponse? = null
    )

    companion object {
        private const val MAX_REFRESH_RETRIES = 3
        private const val DEFAULT_RETRY_DELAY = 500L
        private const val LONG_RETRY_DELAY = 5000L
    }
}