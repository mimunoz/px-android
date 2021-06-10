package com.mercadopago.android.px.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InstructionAction(
    val label: String,
    val url: String?,
    val tag: Tag,
    val content: String?) : Parcelable {

    @Deprecated("Not used anymore")
    object Tags {
        const val LINK = "link"
        const val COPY = "copy"
    }

    enum class Tag {
        @SerializedName("link") LINK,
        @SerializedName("copy") COPY
    }
}
