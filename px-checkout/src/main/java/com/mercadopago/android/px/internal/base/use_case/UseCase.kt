package com.mercadopago.android.px.internal.base.use_case

import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.extensions.orIfEmpty
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

typealias CallBack<T> = (T) -> Unit

abstract class UseCase<in P, out R>(protected val tracker: MPTracker) {

    protected abstract val contextProvider: CoroutineContextProvider
    protected abstract suspend fun doExecute(param: P): Response<R, MercadoPagoError>

    @JvmOverloads
    fun execute(param: P, success: CallBack<R> = {}, failure: CallBack<MercadoPagoError> = {}) {
        CoroutineScope(contextProvider.IO).launch {
            try {
                doExecute(param).also { response ->
                    withContext(contextProvider.Main) {
                        response.resolve(success, failure)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage.orIfEmpty(
                    "Error when execute ${this@UseCase.javaClass.simpleName}")
                withContext(contextProvider.Main) {
                    val error = MercadoPagoError(errorMessage, false)
                    tracker.track(FrictionEventTracker.with(
                        "/use_case",
                        FrictionEventTracker.Id.EXECUTE_USE_CASE,
                        FrictionEventTracker.Style.SCREEN,
                        error))
                    failure(error)
                }
            }
        }
    }

    suspend fun suspendExecute(param: P) = withContext(contextProvider.Default) {
        runCatching {
            doExecute(param).also { response -> withContext(contextProvider.Default) { response } }
        }.getOrElse { withContext(contextProvider.Main) { Response.Failure(MercadoPagoError(it.message.orEmpty(), false)) } }
    }

    open class CoroutineContextProvider {
        open val Default: CoroutineContext by lazy { Dispatchers.Default }
        open val Main: CoroutineContext by lazy { Dispatchers.Main }
        open val IO: CoroutineContext by lazy { Dispatchers.IO }
    }
}
