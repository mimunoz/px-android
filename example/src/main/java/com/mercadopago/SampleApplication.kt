package com.mercadopago

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.multidex.MultiDex
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.mercadopago.android.px.BuildConfig
import com.mercadopago.android.px.addons.*
import com.mercadopago.android.px.di.Dependencies.Companion.instance
import com.mercadopago.android.px.font.FontConfigurator.Companion.configure
import com.mercadopago.android.px.internal.util.HttpClientUtil
import java.util.*

class SampleApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                val (language, country) = localeTag.split("-")
                LocaleContextWrapper.wrap(activity, Locale(language, country))
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
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
        ThreeDSWrapper.initialize()
        val escManagerBehaviour: ESCManagerBehaviour = FakeEscManagerBehaviourImpl()
        val threeDSBehaviour: ThreeDSBehaviour = FakeThreeDSBehaviourImpl()
        val builder = PXBehaviourConfigurer()
        if (BuildConfig.DEBUG) {
            builder.with(MockSecurityBehaviour(escManagerBehaviour))
        }
        builder.with(escManagerBehaviour)
        builder.with(threeDSBehaviour)
            .configure()
        configure()
    }

    companion object {
        var localeTag = "en-US"
    }
}
