package com.mercadopago.android.px.internal.callbacks

import android.net.Uri

internal class DeepLinkHandler(private val deepLinkListener: DeepLinkListener) {

    fun resolveDeepLink(handler: IDeepLinkHandler, uri: Uri) {
        with(handler) {
            listener = deepLinkListener
            resolveDeepLink(uri)
        }
    }
}
