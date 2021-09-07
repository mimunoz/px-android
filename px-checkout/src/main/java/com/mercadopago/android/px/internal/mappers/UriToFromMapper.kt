package com.mercadopago.android.px.internal.mappers

import android.net.Uri
import com.mercadopago.android.px.internal.callbacks.From
import java.util.Locale

private const val FROM = "from"
private const val NONE = "none"

internal object UriToFromMapper {

    fun map(uri: Uri): From {
        val from = uri.getQueryParameter(FROM) ?: NONE
        return From.valueOf(from.toUpperCase(Locale.ROOT))
    }
}
