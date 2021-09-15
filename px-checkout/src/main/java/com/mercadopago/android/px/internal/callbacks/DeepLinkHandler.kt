package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.mappers.UriToFromMapper

internal class DeepLinkHandler(private val listener: DeepLinkListener) {

    private lateinit var wrapper: DeepLinHandlerInterface

    fun resolveDeepLink(uri: Uri) {
        wrapper = createWrapper(uri)
        wrapper.listener = listener
        wrapper.resolveDeepLink(uri)
    }

    private fun createWrapper(uri: Uri): DeepLinHandlerInterface =
        when (UriToFromMapper.map(uri)) {
            From.TOKENIZATION -> TokenizationResponseWrapper()
            From.NONE -> DefaultWrapper()
        }
}
