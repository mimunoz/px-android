package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.display_info.LinkableText

internal class RemediesLinkableMapper : Mapper<LinkableText, com.mercadopago.android.px.model.LinkableText>() {

    override fun map(value: LinkableText): com.mercadopago.android.px.model.LinkableText {

        val linkablePhraselist: ArrayList<com.mercadopago.android.px.model.LinkableText.LinkablePhrase> = ArrayList()
        for (linkablePhrase in value.linkablePhrases) {
            val linkablePhraseRemedies = com.mercadopago.android.px.model.LinkableText.LinkablePhrase(
                linkablePhrase.phrase, linkablePhrase.textColor,
                linkablePhrase.link, linkablePhrase.html, linkablePhrase.installments
            )
            linkablePhraselist.add(linkablePhraseRemedies)
        }

        return com.mercadopago.android.px.model.LinkableText(
            value.text,
            value.textColor,
            linkablePhraselist,
            value.links
        )
    }

    fun mapRemedies(value: LinkableText): com.mercadopago.android.px.model.LinkableText {

        val linkablePhraselist: ArrayList<com.mercadopago.android.px.model.LinkableText.LinkablePhrase> = ArrayList()
        for (linkablePhrase in value.linkablePhrases) {
            val linkablePhraseRemedies = com.mercadopago.android.px.model.LinkableText.LinkablePhrase(
                linkablePhrase.phrase, R.color.px_remedies_link_blue.toString(),
                linkablePhrase.link, linkablePhrase.html, linkablePhrase.installments
            )
            linkablePhraselist.add(linkablePhraseRemedies)
        }

        return com.mercadopago.android.px.model.LinkableText(
            value.text,
            R.color.px_real_black.toString(),
            linkablePhraselist,
            value.links ?: null
        )
    }
}