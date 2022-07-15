package com.mercadopago.android.px.model.one_tap

import android.os.Parcel
import androidx.annotation.StringDef
import com.mercadopago.android.px.internal.util.KParcelable
import com.mercadopago.android.px.internal.util.parcelableCreator

data class CheckoutBehaviour(val modal: String?, val target: String?) : KParcelable {

    private constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(modal)
        writeString(target)
    }

    companion object {
        @JvmField
        var CREATOR = parcelableCreator(::CheckoutBehaviour)
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(Type.START_CHECKOUT, Type.SWITCH_SPLIT, Type.TAP_CARD, Type.TAP_PAY)
    annotation class Type {
        companion object {
            const val START_CHECKOUT = "start_checkout"
            const val SWITCH_SPLIT = "switch_split"
            const val TAP_CARD = "tap_card"
            const val TAP_PAY = "tap_pay"
        }
    }
}