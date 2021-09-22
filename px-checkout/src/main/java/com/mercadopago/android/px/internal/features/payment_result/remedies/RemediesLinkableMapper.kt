package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.R
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
                linkablePhrase.phrase, R.color.px_remedies_link_blue.toString(),
                linkablePhrase.link, linkablePhrase.html, linkablePhrase.installments
            )
            linkablePhraselist.add(linkablePhraseRemedies)
        }

        return LinkableTextRemedies(value.text, R.color.px_real_black.toString(), linkablePhraselist, value.links ?: null)
    }
}