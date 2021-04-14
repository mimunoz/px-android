package com.mercadopago.android.px.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Retry(
    val isNeeded: Boolean,
    val reason: String
) : Parcelable, Serializable