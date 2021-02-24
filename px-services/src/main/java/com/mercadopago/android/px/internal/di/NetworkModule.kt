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

    private var internalRetrofit2: Retrofit? = null
    val retrofitClient2: Retrofit
        get() {
            if (internalRetrofit2 == null) {
                internalRetrofit2 = RetrofitUtil.getRetrofitClient2(applicationContext)
            }
            return internalRetrofit2!!
        }

    fun reset() {
        internalRetrofit = null
    }
}