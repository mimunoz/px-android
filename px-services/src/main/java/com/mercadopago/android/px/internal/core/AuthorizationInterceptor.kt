package com.mercadopago.android.px.internal.core

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class AuthorizationInterceptor(private val authorizationProvider: AuthorizationProvider) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, "Bearer " + authorizationProvider.privateKey)
            .build()
        return chain.proceed(request)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
    }
}