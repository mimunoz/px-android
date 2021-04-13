package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.internal.OneTapItem

internal class CustomOptionIdSolverImpl(private val applicationSelectionRepository: ApplicationSelectionRepository)
    : CustomOptionIdSolver() {

    override fun get(oneTapItem: OneTapItem): String {
        val defaultCustomOptionId = defaultCustomOptionId(oneTapItem)
        val selectedPaymentMethod = applicationSelectionRepository[defaultCustomOptionId].paymentMethod

        return when {
            PaymentTypes.isCardPaymentType(selectedPaymentMethod.type) -> defaultCustomOptionId
            oneTapItem.isOfflineMethods -> oneTapItem.getDefaultPaymentMethodType()
            else -> selectedPaymentMethod.id
        }
    }
}