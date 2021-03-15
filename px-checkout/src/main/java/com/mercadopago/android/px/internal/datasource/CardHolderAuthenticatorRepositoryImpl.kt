package com.mercadopago.android.px.internal.datasource

import android.util.MalformedJsonException
import com.mercadopago.android.px.internal.model.CardHolderAuthenticatorBody
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository
import com.mercadopago.android.px.internal.services.CardHolderAuthenticatorService
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.Currency
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.Site
import com.nds.nudetect.EMVAuthenticationRequestParameters
import java.util.*

class CardHolderAuthenticatorRepositoryImpl(
    private val cardHolderAuthenticatorService: CardHolderAuthenticatorService) : CardHolderAuthenticatorRepository {

    override suspend fun authenticate(paymentData: PaymentData, card: Card, site: Site, currency: Currency, threeDSParams: EMVAuthenticationRequestParameters): Any {
        val body = CardHolderAuthenticatorBody(
            179096502,
            CardHolderAuthenticatorBody.Data(
                paymentData.rawAmount.toString(),
                site.id,
                Date(),
                CardHolderAuthenticatorBody.Card(
                    "JohnDoe Anytown",
                    card.paymentMethod?.id
                ),
                currency.id,
                currency.decimalPlaces,
                threeDSParams.sdkAppID,
                threeDSParams.deviceData,
                JsonUtil.fromJson(threeDSParams.sdkEphemeralPublicKey, CardHolderAuthenticatorBody.SdkEphemPubKey::class.java)
                    ?: throw MalformedJsonException("Malformed sdkEphemeralPublicKey"),
                threeDSParams.sdkReferenceNumber,
                threeDSParams.sdkTransactionID
            )
        )
        return cardHolderAuthenticatorService.authenticate(paymentData.token!!.id, body)
    }
}