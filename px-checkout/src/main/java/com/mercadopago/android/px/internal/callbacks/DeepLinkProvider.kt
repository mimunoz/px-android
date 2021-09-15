package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.mappers.UriToFromMapper

internal object DeepLinkProvider {

    fun createWrapper(uri: Uri): DeepLinkWrapper =
        when (UriToFromMapper.map(uri)) {
            From.TOKENIZATION -> TokenizationResponseWrapper()
            From.NONE -> DefaultWrapper()
        }
}
