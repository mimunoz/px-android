package com.mercadopago.android.px.internal.di

import android.content.Context
import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.core.ApplicationModule
import com.mercadopago.android.px.internal.core.ConnectionHelper
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

    private var _networkApi: NetworkApi? = null
    val networkApi: NetworkApi
        get() {
            if (_networkApi == null) {
                _networkApi = NetworkApi(
                    retrofitClient,
                    ConnectionHelper().also{ it.initialize(applicationContext) })
            }
            return _networkApi!!
        }

    fun reset() {
        internalRetrofit = null
        _networkApi = null
    }
}