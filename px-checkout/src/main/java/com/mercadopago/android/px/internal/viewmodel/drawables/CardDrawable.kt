package com.mercadopago.android.px.internal.viewmodel.drawables

import android.os.Parcelable
import com.mercadopago.android.px.internal.viewmodel.CardUiConfiguration
import kotlinx.android.parcel.Parcelize

@Parcelize
internal class CardDrawable(
    val paymentMethodId: String,
    val cardConfiguration: CardUiConfiguration? = null
) : Parcelable {

    init {
        check(cardConfiguration != null)
    }
}