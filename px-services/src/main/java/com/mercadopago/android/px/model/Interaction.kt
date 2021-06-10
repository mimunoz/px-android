package com.mercadopago.android.px.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Interaction(
    val action: InstructionAction?,
    val title: String,
    val content: String?,
    val showMultilineContent: Boolean?
) : Parcelable
