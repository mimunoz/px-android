package com.mercadopago.android.px.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class AccountMoneyDisplayInfo(
    val sliderTitle: String?,
    val type: AccountMoneyDisplayInfoType?,
    val color: String,
    val paymentMethodImageUrl: String?,
    val message: String?,
    val gradientColors: List<String>?
) : Serializable, Parcelable
