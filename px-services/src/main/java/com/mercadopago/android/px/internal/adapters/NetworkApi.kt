package com.mercadopago.android.px.internal.adapters

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.exception.ExceptionFactory
import com.mercadopago.android.px.internal.base.exception.ExceptionParser
import com.mercadopago.android.px.internal.callbacks.ApiResponse.Success
import com.mercadopago.android.px.internal.callbacks.ApiResponse.Failure
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.model.exceptions.ApiException
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit

internal typealias ApiResponseCallback<R> = com.mercadopago.android.px.internal.callbacks.ApiResponse<R, ApiException>

class NetworkApi(
    private val retrofitClient: Retrofit,
    private val connectionHelper: ConnectionHelper,
    private val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) {

    private fun <D : Any> resolveCallBackResponse(response: Response<D>): ApiResponseCallback<D> {
        return runCatching {
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Success(body)
            } else {
                var failure = Failure(ExceptionFactory.genericError())
                response.errorBody()?.also {
                    failure = Failure(ExceptionParser.parse(response))
                }

                failure
            }
        }.getOrElse { Failure(ExceptionParser.parse(it)) }
    }

    suspend fun <D : Any, T> apiCallForResponse(
        apiServiceClass: Class<T>,
        apiCall: suspend (api: T) -> Response<D>
    ): ApiResponseCallback<D> {
        return withContext(contextProvider.ioDispatcher) {
            if (connectionHelper.hasConnection()) {
                runCatching {
                    apiCall(retrofitClient.create(apiServiceClass))
                }.map(::resolveCallBackResponse).getOrElse { Failure(ExceptionParser.parse(it)) }
            } else Failure(ExceptionFactory.connectionError())
        }
    }
}
