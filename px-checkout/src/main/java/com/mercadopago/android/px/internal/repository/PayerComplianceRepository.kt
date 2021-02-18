package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.PayerCompliance

internal interface PayerComplianceRepository : LocalRepository<PayerCompliance?> {
    fun turnIFPECompliant()

    fun turnedIFPECompliant(): Boolean
}