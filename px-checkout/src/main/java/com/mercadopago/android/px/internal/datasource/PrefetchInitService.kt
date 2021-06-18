package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.features.FeatureProvider
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.internal.CheckoutResponse
import java.util.*

internal class PrefetchInitService(private val checkout: MercadoPagoCheckout,
    private val checkoutService: CheckoutService,
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val trackingRepository: TrackingRepository,
    private val featureProvider: FeatureProvider) {

    suspend fun get(): Response<CheckoutResponse, ApiException> {
        val body = InitRequestBodyMapper(escManagerBehaviour, featureProvider, trackingRepository)
            .map(checkout)

        return checkout.preferenceId?.let {
            Response.Success(checkoutService.checkout(it, checkout.privateKey, body))
        } ?: run {
            Response.Success(checkoutService.checkout(checkout.privateKey, body))
        }
    }
}
