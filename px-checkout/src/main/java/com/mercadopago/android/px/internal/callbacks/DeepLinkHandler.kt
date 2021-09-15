package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.mappers.UriToDeepLinkWrapperMapper.map

internal class DeepLinkHandler(private val deepLinkListener: DeepLinkListener) {

    fun resolveDeepLink(uri: Uri) {
        with(map(uri)) {
            listener = deepLinkListener
            resolveDeepLink(uri)
        }
    }
}
