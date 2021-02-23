package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository.ApplicationSelection
import com.mercadopago.android.px.model.internal.Application

internal interface ApplicationSelectionRepository : LocalRepository<List<ApplicationSelection>> {
    operator fun get(payerPaymentMethodId: String): Application?
    operator fun set(payerPaymentMethodId: String, application: Application)

    data class ApplicationSelection(val payerPaymentMethodId: String, val application: Application)
}