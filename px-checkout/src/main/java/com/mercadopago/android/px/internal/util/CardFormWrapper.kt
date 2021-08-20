package com.mercadopago.android.px.internal.util

import com.mercadolibre.android.cardform.internal.CardFormWeb
import com.mercadolibre.android.cardform.internal.CardFormWithFragment
import com.mercadopago.android.px.internal.core.AuthorizationProvider
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.model.Sites
import java.util.Collections

internal class CardFormWrapper(
    settingRepository: PaymentSettingRepository,
    private val trackingRepository: TrackingRepository,
    private val authorizationProvider: AuthorizationProvider) {

    private val acceptThirdPartyCard = settingRepository.advancedConfiguration.acceptThirdPartyCard()
    private val privateKey = authorizationProvider.privateKey
    private val siteId = settingRepository.site.id
    private val excludedPaymentTypes = settingRepository.checkoutPreference?.excludedPaymentTypes
        ?: Collections.emptyList()

    fun getCardFormWithFragment() = CardFormWithFragment
        .Builder
        .withAccessToken(privateKey, siteId, trackingRepository.flowId)
        .setThirdPartyCard(acceptThirdPartyCard, getActivateCard())
        .setSessionId(trackingRepository.sessionId)
        .setExcludedTypes(excludedPaymentTypes).build()

    fun getCardFormWithWebView() = CardFormWeb
        .Builder
        .withAccessToken(privateKey, siteId, trackingRepository.flowId)
        .setThirdPartyCard(acceptThirdPartyCard, getActivateCard())
        .setSessionId(trackingRepository.sessionId)
        .setExcludedTypes(excludedPaymentTypes).build()

    private fun getActivateCard() = siteId != Sites.ARGENTINA.id
}
