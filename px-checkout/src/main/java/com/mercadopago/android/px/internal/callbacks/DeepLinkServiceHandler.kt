package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.mappers.UriToFromMapper

internal class DeepLinkServiceHandler(private val listener: DeepLinkServiceListener) {

    private lateinit var wrapper: IDeepLinkServiceHandler

    fun resolveDeepLink(uri: Uri) {
        wrapper = createWrapper(uri)
        wrapper.listener = listener
        wrapper.resolveDeepLink(uri)
    }

    private fun createWrapper(uri: Uri): IDeepLinkServiceHandler =
        when (UriToFromMapper.map(uri)) {
            From.TOKENIZATION -> TokenizationResponseWrapper()
            From.NONE -> DefaultWrapper()
        }
}
