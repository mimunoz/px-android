package com.mercadopago.android.px.internal.repository

import com.mercadopago.android.px.model.ExpressMetadata

internal interface ExpressMetadataRepository : LocalRepository<List<@JvmSuppressWildcards ExpressMetadata>> {
    fun sortByState()
}