package com.mercadopago.android.px.model

import android.os.Parcelable
import android.text.TextUtils
import com.mercadopago.android.px.internal.core.extensions.isNotNullNorEmpty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InstructionReference(
    val label: String,
    val fieldValue: List<String>?,
    val separator: String?,
    val comment: String?) : Parcelable {

    @Deprecated("Doesn't belong in a data class")
    fun hasLabel() = label.isNotNullNorEmpty()

    @Deprecated("Doesn't belong in a data class")
    fun hasValue() =  fieldValue?.size != 0

    @Deprecated("Doesn't belong in a data class")
    fun hasComment() = comment.isNotNullNorEmpty()

    @Deprecated("Doesn't belong in a data class")
    fun getFormattedReference(): String {
        val stringBuilder = StringBuilder()
        if (fieldValue != null) {
            for (string in fieldValue) {
                stringBuilder.append(string)
                if (fieldValue.indexOf(string) != fieldValue.size - 1 && !TextUtils.isEmpty(separator)) {
                    stringBuilder.append(separator)
                }
            }
        }
        return stringBuilder.toString()
    }

    @Deprecated("Doesn't belong in a data class")
    fun isNumericReference(): Boolean {
        fieldValue?.forEach {
            if (!TextUtils.isDigitsOnly(it.replace(":", "").replace("-", ""))) {
                return false
            }
        }
        return true
    }
}
