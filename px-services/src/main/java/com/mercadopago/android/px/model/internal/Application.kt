package com.mercadopago.android.px.model.internal

import com.mercadopago.android.px.model.StatusMetadata

data class Application(
    val paymentMethod: PaymentMethod,
    val validationPrograms: List<ValidationProgram>?,
    val status: StatusMetadata
) {

    data class PaymentMethod(
        val id: String,
        val type: String
    )

    data class ValidationProgram(
        val id: String,
        val mandatory: Boolean
    )

    enum class KnownValidationProgram {
        STP;

        companion object {
            operator fun get(validationProgramId: String?): KnownValidationProgram? {
                return values().firstOrNull { it.name.equals(validationProgramId, true) }
            }
        }
    }
}