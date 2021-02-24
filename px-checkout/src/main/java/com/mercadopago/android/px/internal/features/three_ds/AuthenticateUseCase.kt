package com.mercadopago.android.px.internal.features.three_ds

import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository
import com.mercadopago.android.px.internal.util.ThreeDSWrapper
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.Site
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker


class AuthenticateUseCase @JvmOverloads constructor(
        tracker: MPTracker,
        private val threeDSWrapper: ThreeDSWrapper,
        private val cardHolderAuthenticatorRepository: CardHolderAuthenticatorRepository,
        override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<AuthenticateUseCase.Params, String>(tracker) {

    override suspend fun doExecute(param: Params): Response<String, MercadoPagoError> {
        val response = cardHolderAuthenticatorRepository.authenticate(
                param.paymentData,
                param.card,
                param.site,
                param.currency,
                threeDSWrapper.getAuthenticationParameters()
        )

        return Response.Success(response)
    }

    data class Params(
            val paymentData: PaymentData,
            val card: Card,
            val site: Site,
            val currency: Currency
    )
}