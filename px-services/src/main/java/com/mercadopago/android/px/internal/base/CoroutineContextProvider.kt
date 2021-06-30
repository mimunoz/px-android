package com.mercadopago.android.px.internal.base

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Suppress("PropertyName")
open class CoroutineContextProvider {
    open val Main: CoroutineContext by lazy { Dispatchers.Main }
    open val IO: CoroutineContext by lazy { Dispatchers.IO }
    open val Default: CoroutineContext by lazy { Dispatchers.Default }
}