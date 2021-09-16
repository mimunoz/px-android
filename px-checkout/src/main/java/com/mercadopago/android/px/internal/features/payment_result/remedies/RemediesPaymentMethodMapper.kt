package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.internal.features.express.slider.*
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.model.internal.remedies.CardSize

internal class RemediesPaymentMethodMapper(
    private val cardSize: CardSize?
) : Mapper<DrawableFragmentItem, PaymentMethodFragment<*>>() {

    override fun map(drawableFragmentItem: DrawableFragmentItem): PaymentMethodFragment<*> {
        when (cardSize) {
            CardSize.LARGE -> {
                return drawableFragmentItem.draw(PaymentMethodHighResDrawer()) as PaymentMethodFragment<*>
            }
            CardSize.SMALL,
            CardSize.XSMALL -> {
                return drawableFragmentItem.draw(PaymentMethodLowResDrawer()) as PaymentMethodFragment<*>
            }
            CardSize.MINI -> {
                return drawableFragmentItem.draw(PaymentMethodMiniDrawer()) as PaymentMethodFragment<*>
            }
            else -> return drawableFragmentItem.draw(PaymentMethodLowResDrawer()) as PaymentMethodFragment<*>
        }
    }
}