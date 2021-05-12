package com.mercadopago.android.px.addons.internal

import android.content.Context
import com.mercadopago.android.px.addons.LocaleBehaviour

internal class LocaleDefaultBehaviour : LocaleBehaviour {
    override fun attachBaseContext(context: Context): Context {
        return context
    }
}