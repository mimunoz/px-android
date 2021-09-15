package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.mappers.UriToFromMapper

internal object DeepLinkWrapper {

    @JvmStatic
    fun createWrapper(uri: Uri): IDeepLinkHandler =
        when (UriToFromMapper.map(uri)) {
            From.TOKENIZATION -> TokenizationResponseWrapper()
            From.NONE -> DefaultWrapper()
        }
}
