package com.mercadopago.android.px.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class LinkableText(
    val text: String,
    val textColor: String,
    val linkablePhrases: List<LinkablePhrase>,
    val links: Map<String, String>?
) : Parcelable {

    @Parcelize
    data class LinkablePhrase(
        val phrase: String,
        val textColor: String,
        val link: String?,
        val html: String?,
        val installments: Map<String, String>?
    ) : Parcelable {

        fun getLinkId(installments: Int): String? {
            return this.getInstallmentsRemedies()[installments.toString()]
        }

        fun getInstallmentsRemedies(): Map<String, String> {
            return installments ?: emptyMap()
        }
    }
}


