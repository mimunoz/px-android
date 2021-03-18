package com.mercadopago.android.px.internal.di

import android.content.Context
import com.mercadopago.android.px.internal.core.ApplicationModule
import com.mercadopago.android.px.internal.util.RetrofitUtil
import retrofit2.Retrofit

class NetworkModule(context: Context) : ApplicationModule(context) {

    private var internalRetrofit: Retrofit? = null
    val retrofitClient: Retrofit
        get() {
            if (internalRetrofit == null) {
                internalRetrofit = RetrofitUtil.getRetrofitClient(applicationContext)
            }
            return internalRetrofit!!
        }

    fun reset() {
        internalRetrofit = null
    }
}