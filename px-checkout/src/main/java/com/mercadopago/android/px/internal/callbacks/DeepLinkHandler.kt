package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.di.MapperProvider

internal class DeepLinkHandler {

    lateinit var deepLinkListener: DeepLinkListener

    fun resolveDeepLink(uri: Uri) {
        with(MapperProvider.uriToDeepLinkWrapperMapper.map(uri)) {
            listener = deepLinkListener
            resolveDeepLink(uri)
        }
    }
}
