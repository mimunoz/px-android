package com.mercadopago.android.px.internal.callbacks

import android.net.Uri

internal class DefaultWrapper : IDeepLinkServiceHandler() {

    override fun resolveDeepLink(uri: Uri) {
        listener.onDefault()
    }
}
