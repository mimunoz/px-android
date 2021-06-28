package com.mercadopago.android.px

import com.mercadopago.android.px.internal.base.use_case.UseCase
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestContextProvider() : UseCase.CoroutineContextProvider() {
    override var Main: CoroutineContext = Dispatchers.Unconfined
    override var IO: CoroutineContext = Dispatchers.Unconfined

    constructor(io: CoroutineContext, main: CoroutineContext) : this() {
        Main = main
        IO = io
    }

}