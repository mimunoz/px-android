package com.mercadopago.android.px.internal.base

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class CoroutineContextProvider {
    open val mainDispatcher: CoroutineContext by lazy { Dispatchers.Main }
    open val defaultDispatcher: CoroutineContext by lazy { Dispatchers.Default }
    open val ioDispatcher: CoroutineContext by lazy { Dispatchers.IO }
}