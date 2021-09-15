package com.mercadopago.android.px.internal.callbacks

import android.net.Uri

internal class DeepLinkHandler(private val deepLinkListener: DeepLinkListener) {

    fun resolveDeepLink(wrapper: DeepLinkWrapper, uri: Uri) {
        with(wrapper) {
            listener = deepLinkListener
            resolveDeepLink(uri)
        }
    }
}
