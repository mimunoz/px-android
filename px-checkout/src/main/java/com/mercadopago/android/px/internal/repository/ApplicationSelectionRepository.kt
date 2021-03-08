package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.internal.Application
typealias PayerPaymentTypeId = String

internal interface ApplicationSelectionRepository : LocalRepository<MutableMap<PayerPaymentTypeId, Application>> {
    operator fun get(payerPaymentMethodId: String): Application
    operator fun set(payerPaymentMethodId: String, application: Application)
}