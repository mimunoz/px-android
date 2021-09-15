package com.mercadopago.android.px.internal.callbacks

import android.net.Uri

internal abstract class IDeepLinkHandler {

    lateinit var listener: DeepLinkListener

    abstract fun resolveDeepLink(uri: Uri)
}
