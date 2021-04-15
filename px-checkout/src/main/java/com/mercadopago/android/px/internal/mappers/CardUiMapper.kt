package com.mercadopago.android.px.internal.mappers

import com.meli.android.carddrawer.configuration.CardDrawerStyle
import com.meli.android.carddrawer.configuration.FontType
import com.meli.android.carddrawer.configuration.SecurityCodeLocation
import com.mercadopago.android.px.internal.features.security_code.domain.model.BusinessCardDisplayInfo
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.viewmodel.CardUiConfiguration
import com.mercadopago.android.px.model.*

internal object CardUiMapper {

    fun map(cardDisplayInfo: BusinessCardDisplayInfo): CardUiConfiguration {
        with(cardDisplayInfo) {
            return CardUiConfiguration(
                cardholderName,
                expiration,
                cardPatternMask,
                issuerImageUrl,
                paymentMethodImageUrl,
                fontType,
                cardPattern,
                color,
                fontColor,
                securityCodeLocation,
                securityCodeLength,
                null,
                mapCardDisplayInfoTypeToCardDrawerStyle(type)
            )
        }
    }

    fun map(cardDisplayInfo: CardDisplayInfo): CardUiConfiguration {
        with(cardDisplayInfo) {
            return CardUiConfiguration(
                cardholderName,
                expiration,
                getCardPattern(),
                issuerImageUrl,
                paymentMethodImageUrl,
                fontType,
                cardPattern,
                color,
                fontColor,
                securityCode.cardLocation,
                securityCode.length,
                null,
                mapCardDisplayInfoTypeToCardDrawerStyle(type)
            )
        }
    }

    fun map(accountMoneyDisplayInfo: AccountMoneyDisplayInfo): CardUiConfiguration {
        with(accountMoneyDisplayInfo) {
            return CardUiConfiguration(
                TextUtil.EMPTY,
                TextUtil.EMPTY,
                TextUtil.EMPTY,
                null,
                paymentMethodImageUrl,
                FontType.NONE,
                intArrayOf(),
                color,
                null,
                SecurityCodeLocation.NONE,
                0,
                gradientColors,
                mapAccountMoneyDisplayInfoTypeToCardDrawerStyle(type)
            )
        }
    }

    private fun mapAccountMoneyDisplayInfoTypeToCardDrawerStyle(type: AccountMoneyDisplayInfoType?): CardDrawerStyle {
       return when(type) {
           AccountMoneyDisplayInfoType.HYBRID -> CardDrawerStyle.ACCOUNT_MONEY_HYBRID
            else -> CardDrawerStyle.ACCOUNT_MONEY_DEFAULT
        }
    }

    private fun mapCardDisplayInfoTypeToCardDrawerStyle(type: CardDisplayInfoType?): CardDrawerStyle {
        return when(type) {
            CardDisplayInfoType.HYBRID -> CardDrawerStyle.ACCOUNT_MONEY_HYBRID
            else -> CardDrawerStyle.REGULAR
        }
    }
}
