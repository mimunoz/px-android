package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.callbacks.DeepLinkProvider.createWrapper

internal class DeepLinkHandler(private val deepLinkListener: DeepLinkListener) {

    fun resolveDeepLink(uri: Uri) {
        with(createWrapper(uri)) {
            listener = deepLinkListener
            resolveDeepLink(uri)
        }
    }
}
