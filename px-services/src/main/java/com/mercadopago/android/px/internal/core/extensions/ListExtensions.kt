package com.mercadopago.android.px.internal.core.extensions

internal fun <T : Collection<*>> T?.orIfNullOrEmpty(fallback: T): T {
    return this?.takeIf { it.isNotEmpty() } ?: fallback
}