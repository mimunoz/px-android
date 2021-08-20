package com.mercadopago.android.px.internal.core

import android.content.SharedPreferences

class AuthorizationProvider(private val sharedPreferences: SharedPreferences) {

    private var internalPrivateKey: String? = null
    val privateKey: String
        get() {
            if (internalPrivateKey == null) {
                internalPrivateKey = sharedPreferences.getString(PREF_PRIVATE_KEY, null)
            }
            return internalPrivateKey!!
        }

    fun configure(privateKey: String) {
        internalPrivateKey = privateKey
        sharedPreferences.edit().putString(PREF_PRIVATE_KEY, privateKey).apply()
    }

    fun reset() {
        internalPrivateKey = null
        sharedPreferences.edit().remove(PREF_PRIVATE_KEY).apply()
    }

    companion object {
        private const val PREF_PRIVATE_KEY = "PREF_PRIVATE_KEY"
    }
}