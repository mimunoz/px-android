package com.mercadopago.android.px.addons

import android.content.Context
import com.mercadopago.SampleApplication
import java.util.*

internal object FakeLocaleBehaviourImpl : LocaleBehaviour {
    override fun attachBaseContext(context: Context): Context {
        return LocaleContextWrapper.wrap(context, locale)
    }

    override fun getLocale(): Locale {
        val (language, country) = SampleApplication.localeTag.split("-")
        return Locale(language, country)
    }
}