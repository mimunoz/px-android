package com.mercadopago.android.px.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Instruction(
    val title: String,
    val subtitle: String?,
    val info: List<String>?,
    val secondaryInfo: List<String>?,
    val tertiaryInfo: List<String>?,
    val accreditationMessage: String?,
    val type: String?,
    val interactions: List<Interaction>?,
    val accreditationComments: List<String>?,
    val actions: List<InstructionAction>?,
    val references: List<InstructionReference>?
) : Parcelable
