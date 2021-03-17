package com.mercadopago.android.px.internal.datasource

import android.util.MalformedJsonException
import com.mercadopago.android.px.internal.model.CardHolderAuthenticatorBody
import com.mercadopago.android.px.internal.repository.CardHolderAuthenticatorRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.services.CardHolderAuthenticatorService
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.model.PaymentData
import com.nds.nudetect.EMVAuthenticationRequestParameters

class CardHolderAuthenticatorRepositoryImpl(
    private val cardHolderAuthenticatorService: CardHolderAuthenticatorService,
    private val paymentSettingRepository: PaymentSettingRepository) : CardHolderAuthenticatorRepository {

    override suspend fun authenticate(paymentData: PaymentData, threeDSParams: EMVAuthenticationRequestParameters): Any {
        val token = paymentData.token ?: throw IllegalStateException("Missing token during authentication")
        val accessToken = paymentSettingRepository.privateKey ?: return TextUtil.EMPTY
        val body = CardHolderAuthenticatorBody(
            paymentData.rawAmount.toString(),
            CardHolderAuthenticatorBody.Card(
                "JohnDoe Anytown",
                paymentData.paymentMethod.id
            ),
            paymentSettingRepository.currency,
            paymentSettingRepository.site.id,
            threeDSParams.sdkAppID,
            threeDSParams.deviceData,
            JsonUtil.fromJson(threeDSParams.sdkEphemeralPublicKey, CardHolderAuthenticatorBody.SdkEphemPubKey::class.java)
                ?: throw MalformedJsonException("Malformed sdkEphemeralPublicKey"),
            threeDSParams.sdkReferenceNumber,
            threeDSParams.sdkTransactionID

        )
        return cardHolderAuthenticatorService.authenticate(token.id, accessToken, body)
    }
}