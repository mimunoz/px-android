package com.mercadopago.android.px.internal.mappers

import android.net.Uri
import com.mercadopago.android.px.internal.callbacks.DeepLinkWrapper
import com.mercadopago.android.px.internal.callbacks.DefaultWrapper
import com.mercadopago.android.px.internal.callbacks.From
import com.mercadopago.android.px.internal.callbacks.TokenizationResponseWrapper

internal object UriToDeepLinkWrapperMapper {

    fun map(uri: Uri): DeepLinkWrapper =
        when (UriToFromMapper.map(uri)) {
            From.TOKENIZATION -> TokenizationResponseWrapper()
            From.NONE -> DefaultWrapper()
        }
}
