package com.mercadopago.android.px.internal.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class ConnectionHelper {

    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
    }

    fun hasConnection(): Boolean {
        return context?.let { ctx ->
            runCatching {
                val cm = (ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getNetWorkStateFromMarshmallowAndAbove(cm)
                } else {
                    checkConnection()
                }
            }.getOrDefault(false)
        } ?: false
    }

    @Suppress("DEPRECATION")
    fun checkConnection() = try {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                if (networkInfo.isConnectedOrConnecting) {
                    haveConnectedWifi = true
                }
            }
            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.isConnectedOrConnecting) {
                    haveConnectedMobile = true
                }
            }
        }

        haveConnectedWifi || haveConnectedMobile
    } catch (ex: Exception) {
        false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetWorkStateFromMarshmallowAndAbove(cm: ConnectivityManager): Boolean {
        return getNetworkCapabilities(cm)?.let { capabilities ->
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } ?: false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getNetworkCapabilities(cm: ConnectivityManager): NetworkCapabilities? {
        return cm.activeNetwork?.let { cm.getNetworkCapabilities(it) }
    }

    companion object {
        @JvmStatic
        val instance by lazy { ConnectionHelper() }
    }
}