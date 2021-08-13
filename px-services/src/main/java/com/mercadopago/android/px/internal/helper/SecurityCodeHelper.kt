package com.mercadopago.android.px.internal.helper

import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.SecurityCode
import com.mercadopago.android.px.model.exceptions.CardTokenException

private const val MANDATORY = "mandatory"

object SecurityCodeHelper {
    fun isRequired(securityCode: SecurityCode?): Boolean {
        return securityCode?.let { it.length != 0 && it.mode.equals(MANDATORY, true) } ?: false
    }

    fun validate(card: Card, securityCode: String) {
        val cvvLength = card.securityCode?.length ?: 0
        if (cvvLength != 0 && securityCode.trim().length != cvvLength) {
            throw CardTokenException(CardTokenException.INVALID_CVV_LENGTH, cvvLength.toString())
        }
    }
}
