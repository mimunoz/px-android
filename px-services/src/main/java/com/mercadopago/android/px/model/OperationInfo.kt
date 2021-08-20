package com.mercadopago.android.px.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OperationInfo(
    val hierarchy: String,
    val type: String,
    val body: String
) : Parcelable