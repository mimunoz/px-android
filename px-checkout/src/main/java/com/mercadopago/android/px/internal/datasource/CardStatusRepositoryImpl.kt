package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.addons.TokenDeviceBehaviour
import com.mercadopago.android.px.model.internal.CardStatusDM

internal class CardStatusRepositoryImpl(
    private val escManagerBehaviour: ESCManagerBehaviour,
    private val tokenDeviceBehaviour: TokenDeviceBehaviour
) : CardStatusRepository {

    override fun getCardsStatus(): List<CardStatusDM> {
        val cardsWithEsc = escManagerBehaviour.escCardIds
        val tokensStatus = tokenDeviceBehaviour.tokensStatus
        val allCardIds = cardsWithEsc + tokensStatus.map { it.cardId }
        return mutableListOf<CardStatusDM>().also {
            allCardIds.forEach { cardId ->
                val tokenStatusDM = CardStatusDM.TokenStateDM.from(
                    tokensStatus.firstOrNull { it.cardId == cardId }?.state)
                it.add(CardStatusDM(cardId, tokenStatusDM, cardsWithEsc.contains(cardId)))
            }
        }
    }
}