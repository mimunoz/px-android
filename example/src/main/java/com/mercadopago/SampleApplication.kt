package com.mercadopago

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mercadopago.android.px.BuildConfig
import com.mercadopago.android.px.addons.*
import com.mercadopago.android.px.di.Dependencies.Companion.instance
import com.mercadopago.android.px.font.FontConfigurator
import com.mercadopago.android.px.internal.util.HttpClientUtil

class SampleApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        Fresco.initialize(this)
        Stetho.initializeWithDefaults(this)

        // Create client base, add interceptors
        val baseClient = HttpClientUtil.createBaseClient(this, 10, 10, 10)
            .addNetworkInterceptor(StethoInterceptor())

        // customClient: client with TLS protocol setted
        val customClient = HttpClientUtil.enableTLS12(baseClient)
            .build()
        HttpClientUtil.setCustomClient(customClient)
        instance.initialize(applicationContext)
        val escManagerBehaviour: ESCManagerBehaviour = FakeEscManagerBehaviourImpl()
        with(PXBehaviourConfigurer()) {
            if (BuildConfig.DEBUG) {
                with(MockSecurityBehaviour(escManagerBehaviour))
            }
            with(escManagerBehaviour)
            with(FakeLocaleBehaviourImpl)
        }.configure()
        FontConfigurator.configure()
    }

    companion object {
        var localeTag = "en-US"
    }
}
