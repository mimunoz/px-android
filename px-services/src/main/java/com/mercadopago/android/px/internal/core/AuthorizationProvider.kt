package com.mercadopago.android.px.internal.core

import android.content.SharedPreferences

class AuthorizationProvider(private val sharedPreferences: SharedPreferences) {

    val privateKey: String
        get() {
            return "Bearer " + sharedPreferences.getString(PREF_PRIVATE_KEY, null)
        }

    companion object {
        private const val PREF_PRIVATE_KEY = "PREF_PRIVATE_KEY"
    }
}