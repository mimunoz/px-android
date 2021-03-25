package com.mercadopago.android.px.internal.features.express.slider

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.model.PaymentTypes

internal open class CardFragment : PaymentMethodFragment<DrawableFragmentItem>() {
    private lateinit var cardView: CardDrawerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_card, container, false)
    }

    override fun setUpCardDrawerView(cardDrawerView: CardDrawerView) {
        super.setUpCardDrawerView(cardDrawerView)
        model.commonsByApplication.getCurrent().cardDrawable?.let { byApplication ->
            byApplication.cardConfiguration?.takeIf { it.isNotNull() }?.let { card ->
                cardView = cardDrawerView
                cardView.card.name = card.name
                cardView.card.expiration = card.date
                cardView.card.number = card.number
                cardView.show(card)
            }
            byApplication.cardStyle.takeIf { it.isNotNull() }?.let { style ->
                cardView.setStyle(style)
            }
        }
        cardView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
    }

    override fun getAccessibilityContentDescription(): String {
        val builder = SpannableStringBuilder()
        with(model.commonsByApplication.getCurrent()) {
            when {
                PaymentTypes.isAccountMoney(cardDrawable?.paymentMethodId) -> builder
                    .append(description)
                else -> builder
                    .append(cardDrawable?.paymentMethodId)
                    .append(TextUtil.SPACE)
                    .append(issuerName)
                    .append(TextUtil.SPACE)
                    .append(description)
                    .append(TextUtil.SPACE)
                    .append(getString(R.string.px_date_divider))
                    .append(TextUtil.SPACE)
                    .append(cardDrawable?.cardConfiguration?.name)
            }
        }
        return builder.toString()
    }

    override fun disable() {
        super.disable()
        cardView.isEnabled = false
    }

    override fun enable() {
        super.enable()
        cardView.isEnabled = true
    }

    companion object {
        @JvmStatic
        fun getInstance(model: DrawableFragmentItem) = CardFragment().also {
            it.storeModel(model)
        }
    }
}