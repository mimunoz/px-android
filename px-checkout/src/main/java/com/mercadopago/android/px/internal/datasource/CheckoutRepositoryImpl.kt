package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.fold
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.mappers.OneTapItemToDisabledPaymentMethodMapper
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.delay

internal typealias ApiResponseCallback<R> = ApiResponse<R, ApiException>
internal typealias ResponseCallback<R> = Response<R, MercadoPagoError>

internal open class CheckoutRepositoryImpl(
    val paymentSettingRepository: PaymentSettingRepository,
    val experimentsRepository: ExperimentsRepository,
    val disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    val networkApi: NetworkApi,
    val tracker: MPTracker,
    val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    val oneTapItemRepository: OneTapItemRepository,
    val paymentMethodRepository: PaymentMethodRepository,
    val modalRepository: ModalRepository,
    val payerComplianceRepository: PayerComplianceRepository,
    val amountConfigurationRepository: AmountConfigurationRepository,
    val discountRepository: DiscountRepository,
    val initRequestBodyMapper: InitRequestBodyMapper,
    val oneTapItemToDisabledPaymentMethodMapper: OneTapItemToDisabledPaymentMethodMapper
) : CheckoutRepository {

    /* default */
    private var refreshRetriesAvailable = MAX_REFRESH_RETRIES

    override suspend fun checkout(): ResponseCallback<CheckoutResponse> {
        val body = initRequestBodyMapper.map(paymentSettingRepository)
        val preferenceId = paymentSettingRepository.checkoutPreferenceId
        val privateKey = paymentSettingRepository.privateKey
        val apiResponse = networkApi.apiCallForResponse(CheckoutService::class.java) {
            if (preferenceId != null) {
                it.checkout(preferenceId, privateKey, body)
            } else {
                it.checkout(privateKey, body)
            }
        }
        return when (apiResponse) {
            is ApiResponse.Failure -> Response.Failure(
                MercadoPagoError(
                    apiResponse.exception,
                    ApiUtil.RequestOrigin.POST_INIT
                )
            )
            is ApiResponse.Success -> Response.Success(apiResponse.result)
        }

/*
        return networkApi.apiCallForResponse(CheckoutService::class.java) {
            if (preferenceId != null) {
                it.checkout(preferenceId, privateKey, body)
            } else {
                it.checkout(privateKey, body)
            }
        }
 */
    }

    override fun configure(checkoutResponse: CheckoutResponse) {
        if (checkoutResponse.preference != null) {
            paymentSettingRepository.configure(checkoutResponse.preference)
        }
        paymentSettingRepository.configure(checkoutResponse.site)
        paymentSettingRepository.configure(checkoutResponse.currency)
        paymentSettingRepository.configure(checkoutResponse.configuration)
        experimentsRepository.configure(checkoutResponse.experiments)
        payerPaymentMethodRepository.configure(checkoutResponse.payerPaymentMethods)
        oneTapItemRepository.configure(checkoutResponse.oneTapItems)
        paymentMethodRepository.configure(checkoutResponse.availablePaymentMethods)
        modalRepository.configure(checkoutResponse.modals)
        payerComplianceRepository.configure(checkoutResponse.payerCompliance)
        amountConfigurationRepository.configure(checkoutResponse.defaultAmountConfiguration)
        discountRepository.configure(checkoutResponse.discountsConfigurations)
        disabledPaymentMethodRepository.configure(
            oneTapItemToDisabledPaymentMethodMapper.map(checkoutResponse.oneTapItems)
        )
        tracker.setExperiments(experimentsRepository.experiments)
    }

    override suspend fun checkoutWithNewCard(cardId: String): Response<CheckoutResponse, MercadoPagoError> {
        var findCardRes = checkoutAndFindCard(cardId)
        var lastSuccessResponse: Response.Success<CheckoutResponse>? = null
        findCardRes.response.fold(success = { lastSuccessResponse = Response.Success(it) })

        while (cardNotFoundOrRetryNeeded(findCardRes) && hasRefreshRetriesAvailable()) {
            --refreshRetriesAvailable
            delay(findCardRes.retryDelay)
            findCardRes = checkoutAndFindCard(cardId)
            findCardRes.response.fold(success = { lastSuccessResponse = Response.Success(it) })
        }
        refreshRetriesAvailable = MAX_REFRESH_RETRIES

        if (findCardRes.response is Response.Success && !findCardRes.cardFound) {
            return Response.Failure(
                MercadoPagoError(
                    ApiException().also { it.message = "Card not found" },
                    ApiUtil.RequestOrigin.POST_INIT
                )
            )
        }

        return lastSuccessResponse ?: findCardRes.response
    }

    private suspend fun checkoutAndFindCard(cardId: String): FindCardResult =
        when (val response = checkout()) {
            is Response.Success -> findCard(response, cardId)
            is Response.Failure -> FindCardResult(cardFound = false, retryNeeded = true, response = response)
        }


    private fun hasRefreshRetriesAvailable(): Boolean {
        return refreshRetriesAvailable > 0
    }

    private fun cardNotFoundOrRetryNeeded(findCardRes: FindCardResult?): Boolean {
        return (findCardRes == null || !findCardRes.cardFound || findCardRes.retryNeeded)
    }

    private fun findCard(response: Response.Success<CheckoutResponse>, cardId: String): FindCardResult {
        var cardFound = false
        var retryNeeded = false
        for (node in response.result.oneTapItems) {
            if (node.isCard && node.card.id == cardId) {
                cardFound = true
                retryNeeded = node.card.retry.isNeeded
                break
            }
        }
        return FindCardResult(cardFound, retryNeeded, response = response)
    }

    private data class FindCardResult(
        val cardFound: Boolean,
        val retryNeeded: Boolean,
        val retryDelay: Long = if (retryNeeded) LONG_RETRY_DELAY else DEFAULT_RETRY_DELAY,
        var response: ResponseCallback<CheckoutResponse>
    )

    companion object {
        private const val MAX_REFRESH_RETRIES = 3
        private const val DEFAULT_RETRY_DELAY = 500L
        private const val LONG_RETRY_DELAY = 5000L
    }
}