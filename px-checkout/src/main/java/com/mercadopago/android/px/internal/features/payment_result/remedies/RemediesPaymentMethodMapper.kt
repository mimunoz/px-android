package com.mercadopago.android.px.internal.features.payment_result.remedies

import com.mercadopago.android.px.internal.features.express.slider.*
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem

internal class RemediesPaymentMethodMapper(
    private val cardSize: String?
) : Mapper<DrawableFragmentItem, PaymentMethodFragment<*>>() {

    override fun map(drawableFragmentItem: DrawableFragmentItem): PaymentMethodFragment<*> {
        when (cardSize) {
            CardSize.LARGE.getType() -> {
                return drawableFragmentItem.draw(PaymentMethodHighResDrawer()) as PaymentMethodFragment<*>
            }
            CardSize.SMALL.getType(),
            CardSize.XSMALL.getType() -> {
                return drawableFragmentItem.draw(PaymentMethodLowResDrawer()) as PaymentMethodFragment<*>
            }
            CardSize.MINI.getType() -> {
                return drawableFragmentItem.draw(PaymentMethodMiniDrawer()) as PaymentMethodFragment<*>
            }
            else -> return drawableFragmentItem.draw(PaymentMethodLowResDrawer()) as PaymentMethodFragment<*>
        }
    }
}