package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.core.MercadoPagoCheckout
import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.base.extensions.map
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.features.FeatureProvider
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.InitRequestBody
import java.util.*

internal class PrefetchInitService(private val checkout: MercadoPagoCheckout,
    private val networkApi: NetworkApi,
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val trackingRepository: TrackingRepository,
    private val featureProvider: FeatureProvider) {

    suspend fun get(): Response<CheckoutResponse, ApiException> {
        val body = InitRequestBodyMapper(escManagerBehaviour, featureProvider, trackingRepository).map(checkout)

        return when (val apiResponse = getApiResponse(body)) {
            is ApiResponse.Success -> Response.Success(apiResponse.result)
            is ApiResponse.Failure -> Response.Failure(apiResponse.exception)
        }
    }

    private suspend fun getApiResponse(body: InitRequestBody) : ApiResponse<CheckoutResponse, ApiException> {
        return networkApi.apiCallForResponse(CheckoutService::class.java) {
            checkout.preferenceId?.let { prefId ->
                it.checkout(prefId, checkout.privateKey, body)
            } ?: run {
                it.checkout(checkout.privateKey, body)
            }
        }
    }
}
