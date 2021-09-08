package com.mercadopago.android.px.internal.core

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class AuthorizationInterceptor(private val authorizationProvider: AuthorizationProvider) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().let { request ->
            authorizationProvider.privateKey?.let {
                request.newBuilder()
                    .header(AUTHORIZATION_HEADER, "Bearer ${authorizationProvider.privateKey}")
                    .build()
            } ?: request
        })
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
    }
}
