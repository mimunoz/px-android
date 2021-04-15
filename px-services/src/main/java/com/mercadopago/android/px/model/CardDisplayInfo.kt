package com.mercadopago.android.px.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class CardDisplayInfo constructor(
    val cardholderName: String,
    val expiration: String,
    val color: String,
    val fontColor: String,
    val issuerId: Long,
    var cardPattern: IntArray,
    val lastFourDigits: String,
    val paymentMethodImage: String,
    val issuerImage: String?,
    val fontType: String?,
    var paymentMethodImageUrl: String?,
    val issuerImageUrl: String?,
    val securityCode: SecurityCode,
    val type: CardDisplayInfoType
) : Parcelable, Serializable {

    fun getCardPattern(): String {
        val genericPatternBuilder = StringBuilder()
        for (i in cardPattern.indices) {
            for (j in 0 until cardPattern[i]) {

                genericPatternBuilder.append('*')
            }
            if (i != cardPattern.size - 1) {
                genericPatternBuilder.append(' ')
            }
        }

        //Handle last four
        var toProcessLastFour = lastFourDigits.length - 1
        val chars = genericPatternBuilder.toString().toCharArray()
        for (i in chars.size - 1 downTo 1) {
            if (toProcessLastFour >= 0 && chars[i] != ' ') {
                chars[i] = lastFourDigits.toCharArray()[toProcessLastFour]
                toProcessLastFour--
            }
        }
        return String(chars)
    }
}