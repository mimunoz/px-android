package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.CustomSearchItem

internal interface PayerPaymentMethodRepository : LocalRepository<List<@kotlin.jvm.JvmSuppressWildcards CustomSearchItem>> {
    fun getIdsWithSplitAllowed(): Set<String>
}