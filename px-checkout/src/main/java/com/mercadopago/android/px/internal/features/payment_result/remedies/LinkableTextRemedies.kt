package com.mercadopago.android.px.internal.features.payment_result.remedies

import android.os.Parcelable
import com.mercadopago.android.px.model.display_info.LinkablePhrase
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LinkableTextRemedies(
    val text: String,
    val textColor: String,
    val linkablePhrases: List<LinkablePhraseRemedies>,
    val links: Map<String?, String?>?
) : Parcelable {

    @Parcelize
    data class LinkablePhraseRemedies(
        val phrase: String,
        val textColor: String,
        val link: String,
        val html: String?,
        val installments: Map<String?, String?>?
    ) : Parcelable {

        fun getLinkId(installments: Int): String? {
            return this.getInstallmentsRemedies()?.get(installments.toString())
        }

        fun getInstallmentsRemedies(): Map<String?, String?>? {
            return installments ?: emptyMap<String?, String?>()
        }
    }
}


