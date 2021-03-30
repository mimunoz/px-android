package com.mercadopago.android.px.internal.viewmodel.drawables

import android.os.Parcelable
import com.meli.android.carddrawer.configuration.CardDrawerStyle
import com.mercadopago.android.px.internal.viewmodel.CardUiConfiguration
import kotlinx.android.parcel.Parcelize

@Parcelize
internal class CardDrawable(
    val paymentMethodId: String,
    val cardConfiguration: CardUiConfiguration? = null,
    val cardStyle: CardDrawerStyle? = null
) : Parcelable {

    init {
        check(cardConfiguration != null || cardStyle != null)
    }
}