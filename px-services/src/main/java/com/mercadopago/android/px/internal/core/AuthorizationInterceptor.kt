package com.mercadopago.android.px.internal.core

import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthorizationInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor{

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val PREF_PRIVATE_KEY = "PREF_PRIVATE_KEY"
    }

    private val privateKey: String = getPrivateKey()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, privateKey)
            .build()
        return chain.proceed(request)
    }

    private fun getPrivateKey(): String {
        return "Bearer " + sharedPreferences.getString(PREF_PRIVATE_KEY, null)
    }
}