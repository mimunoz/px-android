package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.callbacks.awaitCallback
import com.mercadopago.android.px.internal.features.FeatureProvider
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.InitRequest

internal class PrefetchInitService(private val checkout: MercadoPagoCheckout,
    private val checkoutService: CheckoutService,
    private val cardStatusRepository: CardStatusRepository,
    private val trackingRepository: TrackingRepository,
    private val featureProvider: FeatureProvider) {

    suspend fun get(): Response<CheckoutResponse, ApiException> {
        val checkoutPreference = checkout.checkoutPreference
        val paymentConfiguration = checkout.paymentConfiguration
        val discountParamsConfiguration = checkout.advancedConfiguration.discountParamsConfiguration

        val body = JsonUtil.getMapFromObject(InitRequest.Builder(checkout.publicKey)
            .setCharges(paymentConfiguration.charges)
            .setDiscountParamsConfiguration(discountParamsConfiguration)
            .setCheckoutFeatures(featureProvider.availableFeatures)
            .setCheckoutPreference(checkoutPreference)
            .setFlow(trackingRepository.flowId)
            .setCardsStatus(cardStatusRepository.getCardsStatus())
            .build())

        return checkout.preferenceId?.let {
            checkoutService.checkout(it, checkout.privateKey, body).awaitCallback()
        } ?: run {
            checkoutService.checkout(checkout.privateKey, body).awaitCallback()
        }
    }
}
