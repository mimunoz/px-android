package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.display_info.LinkableText

internal class RemediesLinkableMapper : Mapper<LinkableText, LinkableTextRemedies>() {

    override fun map(value: LinkableText): LinkableTextRemedies {

        val linkablePhraselist: ArrayList<LinkableTextRemedies.LinkablePhraseRemedies> = ArrayList()
        for (linkablePhrase in value.linkablePhrases) {
            val linkablePhraseRemedies = LinkableTextRemedies.LinkablePhraseRemedies(
                linkablePhrase.phrase, linkablePhrase.textColor,
                linkablePhrase.link, linkablePhrase.html, linkablePhrase.installments
            )
            linkablePhraselist.add(linkablePhraseRemedies)
        }

        return LinkableTextRemedies(
            value.text,
            value.textColor,
            linkablePhraselist,
            value.links
        )
    }

    fun mapRemedies(value: LinkableText): LinkableTextRemedies {

        val linkablePhraselist: ArrayList<LinkableTextRemedies.LinkablePhraseRemedies> = ArrayList()
        for (linkablePhrase in value.linkablePhrases) {
            val linkablePhraseRemedies = LinkableTextRemedies.LinkablePhraseRemedies(
                linkablePhrase.phrase, "#000000",
                linkablePhrase.link, linkablePhrase.html, linkablePhrase.installments
            )
            linkablePhraselist.add(linkablePhraseRemedies)
        }

        return LinkableTextRemedies(value.text, "#000000", linkablePhraselist, value.links ?: null)
    }
}