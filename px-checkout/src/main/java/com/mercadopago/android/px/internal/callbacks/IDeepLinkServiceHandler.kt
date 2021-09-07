package com.mercadopago.android.px.internal.callbacks

import android.net.Uri

internal abstract class IDeepLinkServiceHandler {

    lateinit var listener: DeepLinkServiceListener

    abstract fun resolveDeepLink(uri: Uri)
}
