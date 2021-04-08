package com.mercadopago.android.px.model.internal

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CardDrawerSwitch(
    val description: Text,
    val states: SwitchStates,
    val options: List<Option>,
    val switchBackgroundColor: String,
    val pillBackgroundColor: String,
    val safeZoneBackgroundColor: String,
    val default: String
): Parcelable {

    @Parcelize
    data class Text(
        val textColor: String,
        val weight: String,
        val message: String
    ): Parcelable

    @Parcelize
    data class Option(
        val id: String,
        val name: String
    ): Parcelable

    @Parcelize
    data class SwitchStates(
        val checked: State,
        val unchecked: State
    ): Parcelable {

        @Parcelize
        data class State(
            val textColor: String,
            val weight: String
        ): Parcelable
    }
}